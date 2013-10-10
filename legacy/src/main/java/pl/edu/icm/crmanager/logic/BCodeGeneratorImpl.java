package pl.edu.icm.crmanager.logic;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.model.CrmImmutable;
import pl.edu.icm.crmanager.model.CrmProxy;
import pl.edu.icm.crmanager.model.CrmTransparent;
import pl.edu.icm.crmanager.model.Revision;
import pl.edu.icm.crmanager.utils.CrmReflectionUtil;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.common.util.JavassistBuilder;
import pl.edu.icm.sedno.common.util.ReflectionUtil;
import pl.edu.icm.sedno.common.util.Suplement;

import javax.persistence.Id;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Implementacja BCodeGenerator na Javassist
 */
@Service("bCodeGenerator")
public class BCodeGeneratorImpl implements InitializingBean, BCodeGenerator {
    private static final Logger logger = LoggerFactory.getLogger(BCodeGeneratorImpl.class);
   
    private static final Class  CRM_PROXY_INTERFACE = CrmProxy.class;
    private static final Class  DATA_OBJECT_CLASS =   DataObject.class;
    
    private static final String VELOCITY_B_CODE_PROPERTIES = "velocity_BCodeGenerator.properties";
    public  static final String CRM_PROXY_CLASS_SUFFIX = "$javassist_CrmProxy";
   
    /** cache */
    private Map<String, Class> proxyMap = new HashMap<String, Class>(100);
    
    private static boolean initialized = false;
    private static VelocityEngine ve = null;
    private static ClassPool pool    = null;
    private static int rProxyClassCounter = 0;
    private StringResourceRepository repo = null;    
      
    /** for debug only */
    /*private boolean showSource;
    private static StringBuffer debugBuffer = new StringBuffer();
    private Class currentForClass;
    private Class debugForClass;*/
    
    private static Template simpleSetter;    
    private static final String SIMPLE_SETTER_TEMPLATE = 
           "    ${crmProxyUtil}.checkAccessInSetter($0);\n" +
           "    revision.registerValueChange($0, \"$getterName\", $superGetterBoxedValue, $arg1BoxedValue, $retTypeAsToken);\n" +
            "    superInstance.$setterName($1); ";
    
    private static Template simpleGetter;    
    private static final String SIMPLE_GETTER_TEMPLATE = 
            "    return superInstance.$getterName(); ";
        
    private static Template dataObjectSetter;    
    private static final String DATA_OBJECT_SETTER_TEMPLATE =
           "    ${crmProxyUtil}.checkAccessInSetter($0);\n" +
           "    revision.registerValueChange($0, \"$getterName\", superInstance.$getterName(), $1, $retTypeAsToken);\n" +
           "    superInstance.$setterName( $unproxiedArg);\n" +
           "    $thisField = ($getterClass) ${crmProxyUtil}.getBindedProxy($1, ${retTypeProxyClass}.class, ($crmProxyClass)$thisField);\n"
           ;  
    
    /*private static Template dictionaryReferenceSetter;
    private static final String DICTIONARY_REFERENCE_SETTER_TEMPLATE =
           "    ${crmProxyUtil}.checkAccessInSetter($0);\n" +
           "    revision.registerValueChange($0, \"$getterName\", superInstance.$getterName(), $1, $retTypeAsToken);\n" +
           "    superInstance.$setterName( $1 );\n"
           ; 
    */
    
    private static Template dataObjectGetter;
    private static final String DATA_OBJECT_GETTER_TEMPLATE =
            "    if (! ${crmProxyUtil}.isProxyInSync(($crmProxyClass)$thisField, superInstance.$getterName()) ) {\n" +
            "        $thisField = ($getterClass) ${crmProxyUtil}.getBindedProxy(superInstance.$getterName(), ${retTypeProxyClass}.class, null);\n"+
            "    }\n" +
            "    return $thisField;";
            
    private static Template dataObjectCollectionSetter;
    private static final String DATA_OBJECT_COLLECTION_SETTER_TEMPLATE =
            "    ${crmProxyUtil}.checkAccessInSetter($0);\n" +
            "    if ($1 != null && $1 instanceof $AbstractPersistentCollectionClass) {\n" +
            "      //do nothing - hibernate loading\n" + 
            "      return;\n" + 
            "    }\n" + 
            "\n" + 
            "    if ($1 != null && $1 instanceof $CrmCollectionWrapperClass) {\n" + 
            "      throw new pl.edu.icm.crmanager.exception.CrmRuntimeException(\"CrmProxy: can't replace collection with $CrmCollectionWrapperClass, try clear() + addAll()\");\n" + 
            "    }\n" + 
            "\n" + 
            "    //registering: delete all old\n" + 
            "    if ($0.$getterName() != null) {\n" + 
            "      $0.$getterName().clear();\n" + 
            "\n" + 
            "      //registering: add all new\n" + 
            "      if ( $1 != null) {\n" + 
            "        $0.$getterName().addAll($1);\n" + 
            "        $thisField = null;\n" + 
            "      }\n" + 
            "    }\n" + 
            "\n" + 
            "    superInstance.$setterName($1);";
    
    private static Template dataObjectCollectionGetter;
    private static final String DATA_OBJECT_COLLECTION_GETTER_TEMPLATE =     
            "    if ($thisField == null &&\n" +
            "        superInstance.$getterName() != null)\n" + 
            "    {\n" + 
            "        $CrmCollectionWrapperClass newColProxy = ${crmProxyUtil}.buildColWrapper(revision, superInstance, \"$getterName\", superInstance.$getterName(), ${retTypeProxyClass}.class);\n" + 
            "        $thisField = newColProxy;\n" + 
            "    }\n" + 
            "    return $thisField; ";

            
    static Map<Class,String> tokenMap = new HashMap<Class,String>();    
    static 
    {
        tokenMap.put(Integer.TYPE ,  "Integer.TYPE");
        tokenMap.put(Long.TYPE,      "Long.TYPE");
        tokenMap.put(Double.TYPE ,   "Double.TYPE");
        tokenMap.put(Float.TYPE ,    "Float.TYPE");
        tokenMap.put(Boolean.TYPE ,  "Boolean.TYPE");
        tokenMap.put(Character.TYPE ,"Character.TYPE");
        tokenMap.put(Byte.TYPE ,     "Byte.TYPE");
        tokenMap.put(Void.TYPE ,     "Void.TYPE");
        tokenMap.put(Short.TYPE ,    "Short.TYPE");
    }
    
    private void initJavassist() {
        if ( pool == null) {
            pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(this.getClass()));
            //pool.insertClassPath(new ClassClassPath(EnumUserType.class));
        }
    }
    
    private void initVE() {
         if ( ve == null ) { 
            ve = new VelocityEngine();
            Properties p = Suplement.getPropertiesFromClasspath(VELOCITY_B_CODE_PROPERTIES, this.getClass().getClassLoader());
            p.size();
            try {
                ve.init(p);

                repo = StringResourceLoader.getRepository();
            
                repo.putStringResource("simpleSetter", SIMPLE_SETTER_TEMPLATE);
                repo.putStringResource("simpleGetter", SIMPLE_GETTER_TEMPLATE);
                repo.putStringResource("dataObjectSetter", DATA_OBJECT_SETTER_TEMPLATE);
                repo.putStringResource("dataObjectGetter", DATA_OBJECT_GETTER_TEMPLATE);
                repo.putStringResource("dataObjectCollectionSetter", DATA_OBJECT_COLLECTION_SETTER_TEMPLATE);
                repo.putStringResource("dataObjectCollectionGetter", DATA_OBJECT_COLLECTION_GETTER_TEMPLATE);                
                //repo.putStringResource("dictionaryReferenceSetter", DICTIONARY_REFERENCE_SETTER_TEMPLATE);
                            
                simpleSetter = ve.getTemplate("simpleSetter");
                simpleGetter = ve.getTemplate("simpleGetter");
                dataObjectSetter = ve.getTemplate("dataObjectSetter");
                dataObjectGetter = ve.getTemplate("dataObjectGetter");
                dataObjectCollectionSetter = ve.getTemplate("dataObjectCollectionSetter");
                dataObjectCollectionGetter = ve.getTemplate("dataObjectCollectionGetter");
                //dictionaryReferenceSetter = ve.getTemplate("dictionaryReferenceSetter");
                
                logger.info("Velocity initialized" );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }            
        }
    }
    
    public void initialize() {
        if (!initialized) {
            logger.info(">> BCodeGeneratorImpl.init()");
            initJavassist();
            initVE();
            //createUserTypes();
            
            initialized = true;
        }   
    }
    
    public synchronized void afterPropertiesSet() throws Exception {
        initialize();
    }
    
    @Override
    public Class createCrmProxyClass(Class forClass, boolean showSource) {
         
        if (!DATA_OBJECT_CLASS.isAssignableFrom(forClass)) {
            throw new IllegalArgumentException("createCrmProxyClass(): Class ["+forClass.getName()+"] should extend "+DATA_OBJECT_CLASS.getName());
        }
        
        if ( proxyMap.containsKey(getProxyMapKey(forClass)) ) {
            return proxyMap.get(getProxyMapKey(forClass));
        }
            
        String cName = getProxyClassName(forClass);
        
        Class rClass = null;     
        try {
            rClass = Class.forName(cName);           
        } catch (ClassNotFoundException cnf) {
           //ok, lets build it!

            if (JavassistBuilder.existsInPool(cName)) {
                //this means: class is under construction
                return null;
            }
            
            //if (forClass.getSimpleName().equals("Contribution"))
            //	showSource = true;
            JavassistBuilder builder = new JavassistBuilder(cName, forClass, showSource);
            builder.addInterface(CrmProxy.class);
            

            try {
                generateCrmProxyImpl(builder);
                
                rClass = builder.flush();
            } catch (Exception e) {
                builder.printSourceIfExists();
                throw new CrmRuntimeException("Error processing target class ["+forClass.getName()+"]",e);
            }        
        }
                        
        proxyMap.put(getProxyMapKey(forClass), rClass);
                
        return rClass;
    }
    
    private void generateCrmProxyImpl(JavassistBuilder builder) throws Exception {
                
        //CtClass classCt =    pool.get(Class.class.getName());
        Class booleanC  =    Boolean.TYPE;
        //CtClass intCt    =    pool.get(Integer.TYPE.getName());
        
        Class dataObject =    DATA_OBJECT_CLASS;
        Class revision =      Revision.class;
        Class superClass =    builder.getSuperClass();
    
        //1. implementacja interfejsu CrmProxy -----------------------------------------
        builder.debug("-- 1. CrmProxy impl ----------------------------------------");
        builder.generateField("revision", revision);
        builder.generateField("superInstance", superClass );
        builder.generateField("detached", Boolean.TYPE );
        builder.debug("");
        
        builder.generateSimpleGetter("getRevision", revision,     "revision", "CrmProxy Impl");
        builder.generateSimpleGetter("getInstance", dataObject,   "superInstance", "CrmProxy Impl");
        builder.generateSimpleGetter("isDetached",  Boolean.TYPE, "detached", "CrmProxy Impl");
        
        builder.generateSimpleSetter("setRevision", revision,     "revision", "CrmProxy Impl");
        builder.generateSimpleSetter("setInstance", dataObject,   "superInstance", "CrmProxy Impl", superClass);
        builder.generateSimpleSetter("setDetached", Boolean.TYPE, "detached", "CrmProxy Impl");
        //eof 1. implementacja interfejsu CrmProxy
    
           //2. persistent fields
        builder.debug("-- 2. persistent fields mirror -----------------------------");
        for (Method getter : ReflectionUtil.getPersistentGetters(superClass) ) {
            generateProxyField(builder, getter);
        }
        builder.debug("");
        
        //3. konstruktor
        builder.debug("-- 3. Constructor ----------------------------------------------");
        builder.generateEmptyConstructor();
                
        //4. DataObject interface overridden methods
        builder.debug("-- 4. DataObject interface overridden methods-------------------");
        Method IDGetter = ReflectionUtil.getIDGetter(superClass);
        builder.generateComplexGetter("getId", Integer.TYPE, 
                "   return superInstance."+IDGetter.getName()+"();", "@Override, fast getId()");

        builder.generateComplexGetter("getWrappedClass", Class.class,
                "   return superInstance.getWrappedClass();", "@Override");
        
        //5. persistent getters & setters                
        builder.debug("-- 5. persistent getters & setters -----------------------------");
        /**
         * getter powrotny z node'a child do parent
        Method  thisClassParentNodeBackGetter = null;         
        int     thisClassParentNode_Scope = ChildNode.SCOPE_0_PERSIST;
        
        for (Method getter : ReflectionUtil.getPersistentGetters(superClass) ) {
            Method backRefGetter = ReflectionUtil.getOneToManyGetter(getter);

            if (backRefGetter != null && CrmProxyUtil.isChildNodeAnnotationPresent(backRefGetter) ) {
                thisClassParentNodeBackGetter = getter;
                thisClassParentNode_Scope = CrmProxyUtil.getChildNodeAnnotation(backRefGetter).scope();
            }
        }*/        
        
        for (Method getter : ReflectionUtil.getPersistentGetters(superClass) ) { 
        	//logger.info("generateProxyGetterAndSetter() : " + getter);
            generateProxyGetterAndSetter(builder, getter);    
        }
    }
    
    private void generateProxyField (JavassistBuilder builder, Method getter) throws Exception {
        //currentForClass = forClass;
        MethodType mType = determineMethodType(getter);
        
        String thisField = getGetterToProxyField(getter);
        Class retType =    getter.getReturnType();
        
        if (mType == MethodType.dataObjectValue || mType == MethodType.dataObjectCollection ) {
            builder.debug("/** proxy mirror field */");
            builder.generateField(thisField, retType);
        }
    }
    
    /**
     * @param getter - source getter
     */
    private void generateProxyGetterAndSetter(JavassistBuilder builder, Method getter) throws Exception{
        Class forClass = builder.getSuperClass();
        MethodType mType = determineMethodType(getter);
        Class   retType =       getter.getReturnType();
        Class   retTypeWrapped = ReflectionUtil.getReturnDataClass(getter);
        
        Method setter = null;
        VelocityContext context = null;
        if (mType!=MethodType.CrmExcluded) {
                                  
             setter = ReflectionUtil.getterToSetter(getter, forClass);
             context = buildMethodContext(getter, setter);
        }
        
        //logger.info("processing getter: "+ forClass.getName()+"."+getter.getName()+"()");          
        CtMethod cm;
        Class voidC      = void.class;
        
        //generate          
        if (mType == MethodType.simpleValue) {
            if (isAccessible(setter)) {   
                String body = render(simpleSetter, context);
                builder.generateMethod(setter.getName(), voidC, new Class[]{retType}, body, "simpleValue setter");
            }else {
            	throw new CrmRuntimeException("setter " +setter.getDeclaringClass().getSimpleName()+"."+setter.getName()+ "(...) is private, try protected");
            }                      
            
            String body = render(simpleGetter, context);
            builder.generateMethod(getter.getName(), retType, new Class[]{}, body, "simpleValue getter");
        }
        
        /*
        if (mType == MethodType.dictionaryReference) {
            if (setter != null) {   
                cm = new CtMethod(voidC, setter.getName(), new CtClass[]{retTypeCC}, cc);
                setBody(cm, dictionaryReferenceSetter, context, "dictionaryReference setter");
                cc.addMethod(cm);
            }
            
            
            cm = new CtMethod(retTypeCC, getter.getName(), new CtClass[0], cc);
            setBody(cm, simpleGetter, context, " dictionaryReference simple getter");
            cc.addMethod(cm);               
        }*/
        
        if (mType == MethodType.dataObjectValue) {              
            //recursive!
            if (DATA_OBJECT_CLASS.isAssignableFrom(retTypeWrapped) ) {
               createCrmProxyClass(retTypeWrapped, false);
            }
            //currentForClass = forClass;
            
            String body = render(dataObjectGetter, context);
            builder.generateMethod(getter.getName(), retType, new Class[]{}, body, "dataObjectValue getter");
        
            if (isAccessible(setter)) { 
                body = render(dataObjectSetter, context);
                builder.generateMethod(setter.getName(), voidC, new Class[]{retType}, body, "dataObjectValue setter");
            }

        }
        
        if (mType == MethodType.dataObjectCollection) {             
            //recursive!
            if (DATA_OBJECT_CLASS.isAssignableFrom(retTypeWrapped) ) {
                createCrmProxyClass(retTypeWrapped, false);
            }
            
            String body = render(dataObjectCollectionGetter, context);
            builder.generateMethod(getter.getName(), retType, new Class[]{}, body, "dataObjectCollection getter");
        
            if (isAccessible(setter)) { 
                body = render(dataObjectCollectionSetter, context);
                builder.generateMethod(setter.getName(), voidC, new Class[]{retType}, body, "dataObjectCollection setter");
            }
        }           
        
        if (mType == MethodType.transparentProxy) {
        	if (isAccessible(setter)) { 
                builder.generateMethod(setter.getName(), voidC, new Class[]{retType},
                        "    superInstance."+setter.getName()+"($1); \n",
                        //"    revision.addNonCrmModPoint($0);"
                        "transparentProxy setter");
            }
                   
            builder.generateMethod(getter.getName(), retType, new Class[]{},
            		//"    revision.addNonCrmModPoint($0); \n"+ //TODO potrzebna optymalizacja - wykrycie zmian
            		"    return superInstance."+getter.getName()+"();",               
                    "transparentProxy getter");
        }
        
        if (mType == MethodType.CrmExcluded) {
            builder.debug(" -- "+mType+" : "+ getter.getName()+" ");
            builder.debug("");
            //do nothing
        }
    }

	private boolean isAccessible(Method setter) {
		return setter != null && !Modifier.isPrivate(setter.getModifiers());
	}
    
    private String render(Template t, VelocityContext context) {
        StringWriter writer = new StringWriter();
        
        try {          
            t.merge( context, writer );        
        }catch (IOException e) {
           throw new RuntimeException(e);
        }
        
        return writer.toString() ;
    }
    
    private VelocityContext buildMethodContext(Method getter, Method setter) throws NotFoundException  {
        Class   retTypeWrapped = ReflectionUtil.getReturnDataClass(getter);                                   
        
        VelocityContext context = new VelocityContext(); 
        context.put("setterName", (setter == null ? "" : setter.getName()) );
        context.put("getterName", getter.getName());
        context.put("superGetterBoxedValue", getBoxedValue("superInstance."+getter.getName()+"()",retTypeWrapped));
        context.put("arg1BoxedValue", getBoxedValue("$1",retTypeWrapped));
        context.put("retTypeAsToken", getRetTypeAsToken(getter));
        
        //dataObject
        context.put("getterClass",  getter.getReturnType().getName());
        context.put("crmProxyClass",  CRM_PROXY_INTERFACE.getName());
        context.put("crmProxyUtil",   CrmProxyUtil.class.getName());
        context.put("thisField", "$0."+ getGetterToProxyField(getter));
        context.put("retTypeAsToken", getRetTypeAsToken(getter));
        context.put("unproxiedArg", "("+retTypeWrapped.getName()+")"+CrmReflectionUtil.class.getName()+".unproxyC($1)");
        context.put("retTypeProxyClass", getProxyClassName(retTypeWrapped));
        
        // dataObject Collection
        context.put("CrmCollectionWrapperClass", CrmCollectionWrapper.class.getName());
        context.put("AbstractPersistentCollectionClass", AbstractPersistentCollection.class.getName());
        
        return context;
    }
    
    private String getGetterToProxyField(Method getter) {
        return ReflectionUtil.getterToField(getter)+"__";
    }
    
    public static MethodType determineMethodType(Method getter) {
    	if (getter.isAnnotationPresent(CrmImmutable.class)){
            return MethodType.CrmExcluded;
        } else if (getter.isAnnotationPresent(CrmTransparent.class)){
            return MethodType.transparentProxy;                
        } else if (getter.isAnnotationPresent(Id.class)) {
            return  MethodType.transparentProxy;
        } else if ( !ReflectionUtil.isPersistentDataObjectGetter(getter, DATA_OBJECT_CLASS)) {
            return MethodType.simpleValue;
        } else if ( !ReflectionUtil.isCollectionClass(getter.getReturnType()) )
        {
            return MethodType.dataObjectValue;
        }
        else if ( ReflectionUtil.isCollectionClass(getter.getReturnType()) ) { 
            return MethodType.dataObjectCollection;
    //  } else if (dictionaryReference && thisClassParentNode_Scope == 0) {
    //      mType = MethodType.dictionaryReference;
        } else {
            return MethodType.transparentProxy;
        }
    }
    
    private String getProxyMapKey(Class forClass) {
        return forClass.getName();
    }

    private String getProxyClassName(Class forClass) {
        return forClass.getName() + CRM_PROXY_CLASS_SUFFIX;
    }    

    private static String getBoxedValue(String pValue, Class pClass) {
        if (pClass.isPrimitive()) {
            String  retTypeBoxS = ReflectionUtil.getBox( pClass ).getSimpleName();
            return "new "+ retTypeBoxS+ "("+pValue+")";
        }
        return pValue;
    } 
    
    private static String getRetTypeAsToken(Method m) {
        if (tokenMap.containsKey(m.getReturnType())) {
            return tokenMap.get(m.getReturnType());
        }
        
        return m.getReturnType().getName()+".class";
    }
}
