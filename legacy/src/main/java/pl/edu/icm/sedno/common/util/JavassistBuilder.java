package pl.edu.icm.sedno.common.util;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenient builder for CtClass, provides pseudo-source printout 
 * 
 * @author bart
 */
public class JavassistBuilder {
    private static final Logger logger = LoggerFactory.getLogger(JavassistBuilder.class);
    
    private Class        superClass;
    private boolean      showSource;
    private String       genClassName;
    
    private CtClass      genCtClass;
    private Class        outcomeGenClass;
    
    private ClassPool    pool;
    
    private boolean      debugBufferFlushed;
    private StringBuffer debugBuffer;
    private String       debugImplements;

    
    public JavassistBuilder(String  genClassName, Class superClass, boolean showSource) {
        this.genClassName = genClassName;
        this.showSource = showSource;
        this.superClass = superClass;   
 
        if (showSource) {
            debugBuffer = new StringBuffer();
            debugImplements = "";
        }
        
        pool = ClassPool.getDefault();

        if (existsInPool(genClassName)) {
            throw new IllegalArgumentException("Class ["+this.genClassName+"] already exists");
        }

        genCtClass = pool.makeClass(genClassName);
        logger.info(".. JavassistBuilder() : generating class " +genClassName +" ...");    
        
        setSuperClass(superClass);                  
    }
    
    public Class getSuperClass() {
        return superClass;
    }
    
    public Class getOutcomeGenClass() {
        if (outcomeGenClass == null ) {
            throw new RuntimeException("getOutcomeGenClass(): call flush() first!");
        }
        
        return outcomeGenClass;
    }
    
    public void generateField(String fieldName, Class type) {
        CtClass ctType = getFromPool(type.getName());
        
        try {
            genCtClass.addField(new CtField(ctType,  fieldName, genCtClass));
        } catch (CannotCompileException e) {
            throw new RuntimeException("generateField(): error processing class : "+ genClassName, e);
        }
        
        debug("field " + type.getName() + " " + fieldName + ";");
    }
    
    public void generateComplexGetter(String getterName, Class retType, String body, String comment) {
        CtClass retTypeCt = getFromPool(retType.getName());
        
        CtMethod cm = new CtMethod(retTypeCt, getterName, new CtClass[0], genCtClass);
        try {
            setBody(cm, body);  
            genCtClass.addMethod(cm);
        } catch (Exception e) {
            throw new RuntimeException("generateComplexGetter(): error processing class : "+ genClassName, e);
        }
    }
    
    public void generateMethod(String methodName, Class retType, Class[] args, String body, String comment) {
        
        CtClass retTypeCt = getFromPool(retType.getName());
        
        CtClass[] argsCt = new CtClass[args.length];
        for (int i=0; i<args.length; i++) {
            argsCt[i] = getFromPool(args[i].getName());
        }
        
        
        CtMethod cm = new CtMethod(retTypeCt, methodName, argsCt, genCtClass);
        try {
            setBody(cm, body, comment);
            genCtClass.addMethod(cm);
        } catch (Exception e) {
            throw new RuntimeException("generateMethod(): error processing class : "+ genClassName, e);     }
        
    }
    
    public void generateSimpleGetter(String getterName, Class retType, String fieldName, String comment) {
        String body = "   return $0."+fieldName+";";
        
        generateComplexGetter(getterName, retType, body, comment);
    }
    
    public void generateSimpleSetter(String setterName, Class argType, String fieldName, String comment) throws Exception  {
        generateSimpleSetter(setterName, argType, fieldName, comment, null);
    }
    
    public void generateSimpleSetter(String setterName, Class argType, String fieldName, String comment, Class castFieldToClass) throws Exception {
        String castTo = "";
        if (castFieldToClass != null)
            castTo = "("+castFieldToClass.getName()+")";
            
        CtClass voidC = getFromPool("void");
        CtClass argTypeCt = getFromPool(argType.getName());
        
        CtMethod cm = new CtMethod(voidC, setterName, new CtClass[]{argTypeCt}, genCtClass);
        setBody(cm, "   $0."+fieldName+" = "+castTo+"$1;", comment);
        genCtClass.addMethod(cm);
    }
    
    public void generateEmptyConstructor() {
        try {
            CtConstructor ctc = new CtConstructor(new CtClass[0],genCtClass);
            setBody(ctc, "");
            genCtClass.addConstructor(ctc);
        } catch (Exception e) {
            throw new RuntimeException("generateEmptyConstructor(): error processing class : "+ genClassName, e);
        }    
    }
    
    public Class flush() {
        try {
            outcomeGenClass = genCtClass.toClass();
        } catch (CannotCompileException e) {
            throw new RuntimeException("flush(): error processing class : "+ genClassName, e);
        }    
        
        printSourceIfExists();
        
        return outcomeGenClass;
    }
    
    private void setSuperClass(Class superClass) {
        CtClass ctSuperClass;
        try {
            ctSuperClass = pool.get(superClass.getName());
            genCtClass.setSuperclass(ctSuperClass);
        } catch (NotFoundException e) {
            throw new RuntimeException("setSuperClass(): error processing class : "+ genClassName, e);
        } catch (CannotCompileException e) {
            throw new RuntimeException("setSuperClass(): error processing class : "+ genClassName, e);
        }
    }
    
    public void addInterface(Class interf) {
        try {
            CtClass ctInterf = pool.get(interf.getName());
            genCtClass.addInterface(ctInterf);
            debugImplements += interf.getName() +" ";
        } catch (NotFoundException e) {
            throw new RuntimeException("addInterface(): error processing class : "+ genClassName, e);
        }
    }
    
    public void printSourceIfExists() {
        if ( showSource) {
            flushDebugBuffer();
            System.out.println(debugBuffer.toString());
        }
    }
    
    public static CtClass getFromPool(String className) {
        ClassPool pool = ClassPool.getDefault();
        try {
            return pool.get(className);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static boolean existsInPool(String className) {
        ClassPool pool = ClassPool.getDefault();
        try {
            pool.get(className);
            return true;
        } catch (NotFoundException e) {
            return false;
        }       
    }
    
    public void debug(String txt) {
        if ( showSource ) {
            debugBuffer.append(txt+"\n");
        }               
    }
    
    private void debugI(String txt) {
        if ( showSource ) {
            debugBuffer.insert(0,txt+"\n");
        }    
    }
    
    private void flushDebugBuffer() {
        if ( showSource && !debugBufferFlushed) {
            
            debugI("class "+ genClassName +" extends "+ superClass.getName()+" implements "+ debugImplements + " \n{");
            debugI(" */");
            debugI(" * by Javassist ... ");
            debugI("/** ");  
            
            debug("} //eof class ");        
            
            debugBufferFlushed = true;
        }
    }
        
    private void setBody(CtBehavior m, String body) throws Exception{
        setBody(m, body, null);
    }
    
    private void setBody(CtBehavior m, String body, String jDoc) throws Exception {
        // for debug
        StringBuffer args = new StringBuffer();
        if (m.getParameterTypes() != null) {
            int p = 0;
            for (CtClass ct : m.getParameterTypes()) {
                p++;
                args.append(ct.getName() + " $" + p);
                if (p < m.getParameterTypes().length) {
                    args.append(", ");
                }
            }
        }

        if (jDoc != null) {
            debug("/** " + jDoc + " */");

        }

        if (m instanceof CtConstructor) {
            debug("public " + m.getName() + " ( " + args.toString() + " ) {");
        } else {
            debug("method " + ((CtMethod) m).getReturnType().getName() + " "
                    + m.getName() + " ( " + args.toString() + " ) {");
        }

        debug(body);
        debug("}");
        debug("");

        m.setBody("{ " + body + "}");
    }

    
}
