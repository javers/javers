package pl.edu.icm.sedno.common.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import net.sf.cglib.proxy.Enhancer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.icm.sedno.common.model.DataObject;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Zbiór statycznych metod-helperów dla celów refleksji
 * 
 * @author bart
 */
public class ReflectionUtil {
	private static Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);
	   
    /** cache */
    private static Map<String, List<Method>> persistentGettersMap = new ConcurrentHashMap<String, List<Method>>(100);  
    /** cache */
    private static Map<Method, Method> getterToSetterMap =  new ConcurrentHashMap<Method,Method>(100);
    /** cache */
    private static Map<String, Method> idGetterMap =        new ConcurrentHashMap<String, Method>(100);
    /** cache */
    private static Map<String, Method> getterMap =          new ConcurrentHashMap<String, Method>(100);
    /** cache */    
    private static Map<Method, Method> manyToOneGetterMap = new ConcurrentHashMap<Method, Method>(100);
    
    private static Object NULL = new String ("NULL");
    
    public  static final Map<Class, Class> boxingMap = new ImmutableMap.Builder<Class,Class>()
	    .put(Integer.TYPE ,Integer.class)
	    .put(Long.TYPE ,Long.class)
	    .put(Double.TYPE ,Double.class)
	    .put(java.lang.Float.TYPE ,Float.class)
	    .put(Boolean.TYPE ,Boolean.class)    
	    .put(Character.TYPE ,Character.class)
	    .put(Byte.TYPE ,Byte.class)
	    .put(Void.TYPE ,Void.class)
	    .put(Short.TYPE ,Short.class).build();
    
    /**
     * ex. int -> Integer.class
     */
    public static Class getBox(Class primitiveClass) {
        return boxingMap.get(primitiveClass);
    }
       
    /**
     * is Integer, Boolean, ...
     */
    public static boolean isPrimitiveBox(Class clazz) {
    	return boxingMap.containsValue(clazz);
    }
    
    /**
     * Getter z annotacją @Id w klasie someClass,
     * <b>cached</b> 
     */
    public static Method getIDGetter(Class someClass) {
        //cache
        if ( idGetterMap.containsKey(someClass.getName()) ) {
            return idGetterMap.get(someClass.getName());
        }

        Method [] methods  = someClass.getMethods();
        
        for (int i=0; i<methods.length; i++) {
            if ( methods[i].isAnnotationPresent(Id.class) ) {
                idGetterMap.put(someClass.getName(), methods[i]);
                return methods[i];
            }
        }
        return null;
    }
    
    
    /**
     * Wycinek hierarchii klas, które dziedziczą po DataObject + sama klasa DataObject (rootClass)
     * @param baseClass
     */
    public static List<Class> getPersistentHierarchy(Class baseClass) {
        List<Class> ret = new ArrayList<Class>(5);
        
        Class clazz = baseClass;
        while (isClassPersistent(clazz)) {
            ret.add(clazz);
            clazz = clazz.getSuperclass();
        }
        
        return ret;
    }
    
    /**
     * @param baseClass klasa persystentna
     * @return null if not found
     * @throws exception jeśli jest więcej niż jedna annotacja
     */
    public static <T extends Annotation> T getClassAnnotationFromPersistentHierarchy(Class<T> annotationClass, Class baseClass) {
    	
    	T found = null;
    	for (Class c : getPersistentHierarchy(baseClass)) {
    		if (!c.isAnnotationPresent(annotationClass)) continue;
    		
    		if (found !=null) {
    			throw new RuntimeException("more than one @"+annotationClass.getSimpleName()+" is persistent hierarchy of "+ baseClass.getSimpleName());
    		}
    		
    		found = (T) c.getAnnotation(annotationClass);    		
    	}
    	
    	return found;
    }
    
    public static boolean isClassPersistent(Class clazz) {
        return (clazz.getAnnotation(Entity.class) != null || clazz.getAnnotation(MappedSuperclass.class) != null || clazz.getAnnotation(Embeddable.class) != null);
    }
    
    /**
     * Lista (ImmutableList) persistent getterów dla danej klasy
     * see {@link #isPersistentGetter(Method)}
     * <b>cached</b>
     */  
    public static List<Method> getPersistentGetters(Class baseClass) {
        //cache
        if ( !persistentGettersMap.containsKey(baseClass.getName()) ) {
   
            List<Method> list = new ArrayList<Method>(50);
            
            for (Class methodSrc : getPersistentHierarchy(baseClass)) {
                for (Method m : methodSrc.getDeclaredMethods()) {
                    if (isPersistentGetter(m)) {
                        list.add(m);
                    }
                }
            }
            ImmutableList.Builder builder = new ImmutableList.Builder();
            builder.addAll(list);
            
            List<Method> immutableList = builder.build();
            persistentGettersMap.put(baseClass.getName(), immutableList);
        }
        return persistentGettersMap.get(baseClass.getName());
    }
    
    /**
     * Wyciąga klasę wyniku metody:
     * jeśli wynikiem jest obiekt -  klasę obiektu,
     * jeśli wynikiem jest Generic Set lub List - klasę obiektów kolekcji 
     */
    public static Class getReturnDataClass(Method m) {      
        if ( m.getReturnType() == m.getGenericReturnType() ) {
            return m.getReturnType();
        } 
        else {
            ParameterizedType ptype = (ParameterizedType)m.getGenericReturnType();
            Type[] targs = ptype.getActualTypeArguments();
            
            if (ParameterizedType.class.isAssignableFrom(targs[0].getClass())) {
                return (Class)((ParameterizedType)targs[0]).getRawType();
            }
            else if ( targs[0].getClass() == Class.class) {
                return (Class)targs[0]; 
            } 
            else {
                throw new IllegalArgumentException(
                    "Dont know how to handle class: "+
                    m.getGenericReturnType().toString()+", method:"+m.getName());
            }
        }
    }
    
    /**
     * True jeśli klasa jest kolekcją 
     */
    public static boolean isCollectionClass(Class clazz)
    {
        if (Collection.class.isAssignableFrom(clazz)) {
            return true;
        }     
        return false;
    }
    
    /**
     * true jeśli returnType to ParameterizedType
     */
    public static boolean isGenericReturnType(Method m) {    	
    	return (m.getGenericReturnType() instanceof ParameterizedType); 
    }
    
    /**
     * true jeśli type to ParameterizedType
     */
    public static boolean isGenericType(Type type) {    	
    	return (type instanceof ParameterizedType); 
    }
    
    /**
     * jeśli isGenericReturnType() - pierwszy z listy argumentów returnType, wpp null 
     */
    public static Type getReturnTypeParameter(Method m) {
    	if (! isGenericReturnType(m)) return null;
    	
    	return getReturnTypeParameters(m)[0];
    }
    
    /**
     * jeśli isGenericType() - pierwszy z listy argumentów type, wpp null 
     */
    public static Type getTypeParameter(Type type) {
    	if (! isGenericType(type)) return null;
    	
    	ParameterizedType genericReturnType = (ParameterizedType)type;
    	
    	return genericReturnType.getActualTypeArguments()[0];
    }
    
    /**
     * jeśli isGenericReturnType() - lista argumentów returnType, wpp [] 
     */
    public static Type[] getReturnTypeParameters(Method m) {
    	if (! isGenericReturnType(m)) 
    		return new Type[]{};
    	
    	ParameterizedType genericReturnType = (ParameterizedType)m.getGenericReturnType();
		
		return genericReturnType.getActualTypeArguments();
    }
    
     
    /**
     * True jeśli isPersistentGetter 
     * i returnType extenduje rootClass lub jest kolekcją obiektów extendujących rootClass
     */
    public static boolean isPersistentDataObjectGetter(Method m, Class rootClass) {
        return (isPersistentGetter(m) &&
                DataObject.class.isAssignableFrom(ReflectionUtil.getReturnDataClass(m)) );      
    }
    
    /**
     * True jeśli metoda jest getterem i nie ma annotacji Transient
     * 
     * @param rootClass DataObject class
     */
    public static boolean isPersistentGetter(Method m) {
    	Preconditions.checkNotNull(m);
    	
    	if (m.getName().length() < 3) {
    		return false;
    	}
    	
        if (  isGetter(m) &&
              isClassPersistent( m.getDeclaringClass()) &&
              m.isAnnotationPresent(Transient.class) == false &&
              Modifier.isAbstract(m.getModifiers()) == false && 
              Modifier.isVolatile(m.getModifiers()) == false              
           )
        {           
            return true;
        }
        else {
            return false;
        }
    }
    
    public static boolean isGetter(Method m) {
        if (m.getParameterTypes().length > 0) {
            return false;
        }

    	return ( ( m.getName().substring(0,3).equals("get") &&
                   m.getReturnType() != Boolean.TYPE ) ||
                 ( m.getName().substring(0,2).equals("is") &&
                   m.getReturnType() == Boolean.TYPE )
               );
    }
        
    public static boolean isHibernateProxy(Object obj) {
        return (obj instanceof HibernateProxy);
    }
    
    public static boolean isHibernateColProxy(Object obj) {
        return (obj instanceof PersistentCollection);
    }
    
    /**
     * Jeśli obj jest HibernateProxy zwraca proxowany obiekt, wpp obj
     */    
	public static <T> T unproxyH(T obj) {
		if (obj == null) {
			return null;
		}
		
        if (isHibernateProxy(obj)) {
        	@SuppressWarnings("unchecked")
			T ret = (T)((HibernateProxy)obj).getHibernateLazyInitializer().getImplementation(); 
            return ret;
        }
        
        return  obj;
    }
			
	/**
	 * EXPERIMENTAL impl
	 * 
	 * Zdziera hibernateProxy z persistent getterów danetgo obiektu,
	 * kolekcje persystentnte zastępuje zwykłymi
	 */
	public static Object unproxyHReferences(Object obj) {
		Set<Integer> visited = Sets.newHashSet();
		
		Object result = unproxyHReferences__(obj, visited, "/", 0);
		
		logger.debug("done unproxyHReferences("+obj+"), "+visited.size()+" object(s) visited");
		
		return result;
	}
	
	/**
	 * EXPERIMENTAL impl
	 */
	private static Object unproxyHReferences__(Object obj, Set<Integer> visited, String path, int lvl) {
		if (visited.contains(System.identityHashCode(obj))) {
			return obj;
		}
		
		obj = unproxyH(obj);
		
		visited.add(System.identityHashCode(obj));
		
		System.out.println("unproxyHReferences__(obj:"+obj.getClass().getSimpleName()+"#"+System.identityHashCode(obj)+", path:"+path+")");
				
		for (Field field : getAllFields(obj.getClass())) {
			
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			
        	if (Modifier.isPrivate(field.getModifiers()) ||
            	Modifier.isProtected(field.getModifiers()))
            {
        		field.setAccessible(true);
            }   
			
        	Object ref = fieldGet(field, obj);
			
			if (ref == null){
				continue;
			}
					
			if (isHibernateColProxy(ref)) {
				
				if (!Hibernate.isInitialized(ref)) {
					fieldSet(field, obj, null); //not sure about that
					continue;
				}
									
				//System.out.println(StringUtils.repeat(".. ",lvl)+ "unproxying collection "+field.getName()+", obj: "+ obj.getClass().getSimpleName()+"#"+System.identityHashCode(obj));
				
				if (Map.class.isAssignableFrom( field.getType() )) {
					Map freshMap = Maps.newHashMap((Map)ref); //only map of values is supported
					fieldSet(field, obj, freshMap);
					continue;
				}
				
				Collection freshCol = null;
				if (Set.class.isAssignableFrom( field.getType() )) {
					freshCol = Sets.newHashSet();
				} else if (List.class.isAssignableFrom( field.getType() )) {
					freshCol = Lists.newArrayList();					
				} else {
					throw new NotImplementedException("unproxyHReferences() : collection "+ field.getType() + " is not supported"); 
				}
									
				for (Object element : (Collection)ref) {
					freshCol.add(unproxyHReferences__(element, visited, path+"."+field.getName(), lvl+1));
				}
				fieldSet(field, obj, freshCol);
			}
			else if (isHibernateProxy(ref)) {		
				
				if (!Hibernate.isInitialized(ref)) {
					fieldSet(field, obj, null); //not sure about that
					continue;
				}
				
				//System.out.println(StringUtils.repeat(".. ",lvl)+ "unproxying ref "+field.getName());		
				fieldSet(field, obj, unproxyHReferences__(ref, visited, path+"."+field.getName(), lvl+1));
				
			} else if (ref instanceof Collection) { //proxy mogą siedzieć niżej
				//System.out.println(StringUtils.repeat(".. ",lvl)+ "recursive call, iterate collection "+field.getName());
				
				for (Object element : (Collection)ref) {
					unproxyHReferences__(element, visited, path+"."+field.getName(), lvl+1);
				}
			} else if (isClassPersistent(ref.getClass())) { //proxy mogą siedzieć niżej				
				//System.out.println(StringUtils.repeat(".. ",lvl)+ "recursive call, ref field: "+field.getName()+", ref: "+ref.getClass().getSimpleName()+"#"+System.identityHashCode(ref));
				
				fieldSet(field, obj, unproxyHReferences__(ref, visited, path+"."+field.getName(), lvl+1));				
			} else {
				//System.out.println(StringUtils.repeat(".. ",lvl)+ "filed skipped: "+field.getDeclaringClass().getSimpleName()+"."+field.getName());
			}
		}
		return obj;		
	}
	
	public static Object fieldGet(Field field, Object onObject) {
    	if (Modifier.isPrivate(field.getModifiers()) ||
           	Modifier.isProtected(field.getModifiers()))
        {
    		field.setAccessible(true);
        }   	
    	
    	try {
			return field.get(onObject);
		} catch (Exception e) {
			throw new RuntimeException("error calling field.get() on "+ field.getDeclaringClass().getSimpleName()+"."+field.getName(),e);
		}
	}
	
	public static void fieldSet(Field field, Object onObject, Object value) {
    	if (Modifier.isPrivate(field.getModifiers()) ||
           	Modifier.isProtected(field.getModifiers()))
        {
    		field.setAccessible(true);
        }   	
    	
    	try {
			field.set(onObject,value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
        
    /**
     * Returns the base class if the clazz is a CGLIB proxy.
     * Returns the clazz otherwise.
     */
    public static Class<?> unproxySpringAspect(Class<?> clazz) {
        if (Enhancer.isEnhanced(clazz) /* spring ver <= 3.1 */ || org.springframework.cglib.proxy.Enhancer.isEnhanced(clazz) /* spring ver >= 3.2 */) {
            return clazz.getSuperclass();
        }
        return clazz;
    }
    
    
    /**
     * Finds getter method even if declared as private
     * 
     * <b>cached</b>
     * 
     * @throws RuntimeException if NoSuchMethodException
     */
    public static Method findGetter(String getterName, Class methodSource) {
        String key = getterName + "|" + methodSource.getName();
        
        if (!getterMap.containsKey(key))
        {
            if (getterName.contains(".")) {
                String[] chunks = getterName.split("\\.");
                Class cursorClss = methodSource;
                Method cursorMethod = null;
                for (String getterChunk : chunks) {
                    cursorMethod = findGetter(getterChunk, cursorClss);
                    cursorClss = cursorMethod.getReturnType();
                }
                return cursorMethod;
            }
            
            Class[]  parameterTypes = new Class[0];
            Method getter = findMethodEvenIfPrivate(getterName, parameterTypes, methodSource);
                   
            getterMap.put(key, getter);
        }
        return getterMap.get(key);
    }
    
    /**
     * All fields, even if inherited or private
     */
    public static List<Field> getAllFields(Class methodSource) {
    	Class cursor = methodSource;
    	List<Field> fields = Lists.newArrayList();
    	while (Object.class != cursor) {
    		CollectionUtils.addAll(fields, cursor.getDeclaredFields());
    		
    		cursor = cursor.getSuperclass();
    	}
    	return fields;
    }
    
    /**
     * ex.
     * Document getDocument() -> setDocument(Document) <br/><br/>
     *  
     * Jeśli setter nie zostanie znaleziony w methodSrc, przeszukiwana jest superklasa <br/><br/>
     * 
     * <b>cached</b>
     * @param methodSrc jeśli null -> getter.getDeclaringClass()
     */
    public static Method getterToSetter(Method getter, Class methodSrc) {
        if (getterToSetterMap.containsKey(getter)) {
        	Object value = getterToSetterMap.get(getter);
        	if (value == NULL) {
        		return null;
        	} else {
        		return getterToSetterMap.get(getter);
        	}            
        }
                
        if (methodSrc == null) {
            methodSrc = getter.getDeclaringClass();
        }
            
        Class[]  parameterTypes = new Class[1];
        parameterTypes[0] = getter.getReturnType();
    
        Method setter = null;
        try {
            setter = findSetter(getter.getName(), parameterTypes, methodSrc);
        }catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        
        if (setter == null) {
        	((Map)getterToSetterMap).put(getter, NULL);
        }
        else {
        	getterToSetterMap.put(getter, setter);
        }
        
        return setter;
    }

    /**
     * ex: getCode() -> code,
     *     isTrue()  -> true
     */
    public static String getterToField(Method getter) {
        
        if (getter.getName().substring(0,3).equals("get")) {
            return getter.getName().substring(3,4).toLowerCase()+getter.getName().substring(4);
        }
        
        if (getter.getName().substring(0,2).equals("is")) {
            return getter.getName().substring(2,3).toLowerCase()+getter.getName().substring(3);
        }
        
        throw new IllegalArgumentException("Method ["+getter+"] is not getter");
    }
    
    public static String classToField(Class clazz) {
        String fName = clazz.getSimpleName();
        return fName.substring(0,1).toLowerCase()+fName.substring(1);
    }
    
    /**
     * Znajduje metodę Visitor.visit(Object), której arg ''najlepiej'' pasuje do klasy 
     * visitable
     * 
     * @author bart
     */
    public static Method findVisitorMethod(Class visitable, Class visitor) throws NoSuchMethodException {
        Class clazz = visitable;
        
        //search in class hierarchy
        while (clazz != null) {
            //try class
            try {
                Method visit = visitor.getMethod("visit", new Class[]{clazz});
                return visit;
            } catch (Exception e) {
                //go on
            }
            
            //try interfaces
            for (Class interf : clazz.getInterfaces()) {
                try {
                    Method visit = visitor.getMethod("visit", new Class[]{interf});
                    return visit;
                } catch (Exception e) {
                    //go on
                }
            }
            
            //step up in class hierarchy
            clazz = clazz.getSuperclass();
        }
        
        //search in interfaces

        throw new NoSuchMethodException ("visit("+visitable.getSimpleName()+") method not found in visitor class "+visitor.getClass().getName());
    }
    
    /**
     * Zwraca listę metod spełniających podane kryteria
     * @param clazz Interesująca klasa
     * @param nameRegex Wyrażenie regularne określające nazwę metody
     * @param returnType Typ jaki metoda musi zwracać. Jeśli null - nie brany pod uwagę.
     * @param paramTypes Lista parametrów, które musi zawierać metoda. Uwaga! liczy się kolejność parametrów i
     * dokładna ich liczba.
     * @author luc
     */
    public static List<Method> getMethods(Class<?> clazz, String nameRegex, Class<?> returnType, Class<?>... paramTypes) {
    	
    	List<Method> fitMethods = new ArrayList<Method>();
    	
    	Method[] methods = clazz.getMethods();
    	
    	for (Method method : methods) {
    		
    		if (!method.getName().matches(nameRegex)) continue;
    		
    		if (returnType != null) {
    			if (!method.getReturnType().equals(returnType)) continue;
    		}
    		
    		if (paramTypes.length != method.getParameterTypes().length) continue;
    		
    		if (Arrays.equals(paramTypes, method.getParameterTypes())) {
    			fitMethods.add(method);
    		}
    		
    	}
    	
    	return fitMethods;
    }
    
    /**
     * Returns methods of the clazz that are annotated with the annotationClass annotatation.
     * Returns empty list if no method with the given annotation has been found.
     */
    public static List<Method> getMethodsWithAnnotationPresent(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Method> methodsWithAnnotation = new ArrayList<Method>();
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                methodsWithAnnotation.add(method);
            }
        }
        return methodsWithAnnotation;
    }
    
    public static boolean isPersistentGetterOwnerOfRelation(Method getter) {
        if (getter.isAnnotationPresent(ManyToOne.class))
            return true;
        
        if (getter.isAnnotationPresent(Embeddable.class))
            return true;
               
        OneToMany ann = getter.getAnnotation(OneToMany.class);
        if (ann != null && ann.mappedBy() != null)
            return false;

        OneToOne annO = getter.getAnnotation(OneToOne.class);
        if (annO != null && StringUtils.isEmpty(annO.mappedBy()))
            return true;

        
        throw new RuntimeException("isPersistentGetterOwnerOfRelation("+getter+")? don't konw");
    }
     
    public static Method findMethodEvenIfPrivate(String methodName, Class[] parameterTypes, Class methodSrc) {
    	Method ret = null;
    	try {
        	Class methodSourceCursor = methodSrc;
        	//pętla jest potrzebna, ponieważ Class.getMethod() nie widzi metod prywatnych
        	while(methodSourceCursor != null) { 
        		try {
        			ret = methodSourceCursor.getDeclaredMethod(methodName, parameterTypes);
        			break;
        		} catch (java.lang.NoSuchMethodException consumed) {
        			//go up
        			methodSourceCursor = methodSourceCursor.getSuperclass();
				}
        	}
        	
        	if (ret == null) {
        		throw new java.lang.NoSuchMethodException(methodSrc.getName()+"."+methodName+"(...)");
        	}
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    	return ret;
    } 
    
    //-- private
    private static Method findSetter(String getterName, Class[] parameterTypes, Class methodSrc) 
    throws NoSuchMethodException
    {
       Method setter = null;
       
       if (getterName.substring(0,2).equals("is")) {
    	   setter = findMethodEvenIfPrivate("set"+getterName.substring(2), parameterTypes, methodSrc);
       } else {
           setter = findMethodEvenIfPrivate("s"+getterName.substring(1), parameterTypes, methodSrc);
       }
       return setter;
    }    
  
    
    /**
     * Wyciąga gettera z przeciwnej strony relacji dwustronnej
     * typu OneToMany -> ManyToOne <br/>
     * <b>cached</b>
     */
    public static Method getManyToOneGetter(Method oneToManyMethod) {
        if (manyToOneGetterMap.containsKey(oneToManyMethod)) {
            return manyToOneGetterMap.get(oneToManyMethod);
        }
            
        String mappedBy = getMappedBy(oneToManyMethod);     
        
        if (mappedBy == null) {
            manyToOneGetterMap.put(oneToManyMethod, null);
            return null;
        }
        
        Class childClass = ReflectionUtil.getReturnDataClass(oneToManyMethod);
        
        Method getter;
        try {
            getter = childClass.getMethod(fieldToGetterName(mappedBy), new Class[0]);
        }
        catch (NoSuchMethodException e) {
            getter = null;
        }
        
        manyToOneGetterMap.put(oneToManyMethod, getter);
        return getter;                  
    }
    
    /**
     * @see #getManyToOneGetter
     * <b>cached</b> 
     */
    public static Method getBackRefSetter(Method oneToManyMethod) {
        Method backGetter = getManyToOneGetter(oneToManyMethod);
        return getterToSetter(backGetter, null);
    }
    
    /**
     * calls String one arg Constructor
     */
    public static  <T extends Object> T invokeConstructor(Class<T> clazz, String arg) {
        try {
            Constructor<T> c = clazz.getConstructor(new Class[]{String.class});
            return c.newInstance(new Object[]{arg});
        } catch (Exception e) {
            throw new RuntimeException("error calling Constructor "+ clazz.getSimpleName() +"("+arg+")",e);
        }
    }
    
	public static <T extends Object> T[] invokeArrayConstructor(Class<T[]> clazz, int length) {
		Class componentClass = clazz.getComponentType();
		Object result = Array.newInstance(componentClass, length);
		return (T[]) result;
	}
    
    /**
     * calls default, no arg Constructor
     */
    public static  <T extends Object> T invokeConstructor(Class<T> clazz) {
        try {
            Constructor<T> c = clazz.getConstructor(new Class[]{});
            return c.newInstance(new Object[]{});
        } catch (Exception e) {
            throw new RuntimeException("error calling Constructor "+ clazz.getSimpleName() +"()",e);
        }
    }
    
    public static void invokeSetter(Method setter, Object onObject, Object value) {
        try {
            setter.invoke(onObject, new Object[]{value});
        } catch (Exception e) {
            throw new RuntimeException("error calling setter: "+ setter,e);
        } 
    }
    
    
    public static void invokeSetterEvenIfPrivate(Method setter, Object onObject, Object value) {
        try {
        	if (Modifier.isPrivate(setter.getModifiers()) ||
        		Modifier.isProtected(setter.getModifiers()))
        	{
        			setter.setAccessible(true);
            }   
        	
            setter.invoke(onObject, new Object[]{value});
        } catch (Exception e) {
            throw new RuntimeException("error calling setter",e);
        } 
    }

    
    public static Object invokeGetter(Method getter, Object onObject) {
        try {
            return getter.invoke(onObject, new Object[]{});
        } catch (Exception e) {
            throw new RuntimeException("error calling getter",e);
        }
    }
    
    public static Object invokeGetterEvenIfPrivate(Method getter, Object onObject) {
    	try {        	
    		if (Modifier.isPrivate(getter.getModifiers()) ||
    			Modifier.isProtected(getter.getModifiers()))
    		{
        		getter.setAccessible(true);
        	}        	
            return getter.invoke(onObject, new Object[]{});
        } catch (Exception e) {
            throw new RuntimeException("error calling getter",e);
        }
    }
    
    private static String fieldToGetterName(String fieldName) {
        return "get" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1,fieldName.length());        
    }
    
    /**
     * Wyciąga wartość z annotacji OneToMany.mappedBy.
     */
    private static String getMappedBy(Method m) {
        //czy metoda ma annotację @OneToMany?
        OneToMany oneToManyAnnotation = m.getAnnotation( OneToMany.class ); 
        if (oneToManyAnnotation == null) return null;
        
        return oneToManyAnnotation.mappedBy();        
    }
    
    public static Object invokePrivateMethod(Object o, String methodName, Object[] params) {
        final Method methods[] = o.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; ++i) {
            if (methodName.equals(methods[i].getName())) {
                try {
                    methods[i].setAccessible(true);
                    return methods[i].invoke(o, params);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        throw new RuntimeException("Method '" + methodName + "' not found");
    } 
    
	public static Class forName(String className) {
		Class clazz = null;
		try {
			clazz = Class.forName(className);
			
		}catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return clazz;
	}
    
    /**
     * see {@link #getManyToOneGetter(Method)}
     * <b>not cached</b>
     *
    public static Method getOneToManyGetter(Method manyToOneMethod) {
        
        Class parentClass = ReflectionUtilPure.getReturnDataClass(manyToOneMethod);
        
        Iterator<Method> it =  getPersistentGetters(parentClass);
        while (it.hasNext()) {
            Method m = it.next();
            if ( getMappedBy(m) != null && 
                 getMappedBy(m).equals( ReflectionUtilPure.getterToField(manyToOneMethod)) &&
                 ReflectionUtilPure.getReturnDataClass(m).equals(manyToOneMethod.getDeclaringClass()))
            {
                return m;
            }
        }       
        return null;        
    }*/
    
}
