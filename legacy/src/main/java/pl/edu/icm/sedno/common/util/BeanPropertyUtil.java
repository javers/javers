package pl.edu.icm.sedno.common.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

import static pl.edu.icm.sedno.common.util.ReflectionUtil.getTypeParameter;
import static pl.edu.icm.sedno.common.util.ReflectionUtil.isGenericType;


/**
 * zbiór helperów działających głównie na klasie PropertyDescriptor 
 * @author bart
 */
public class BeanPropertyUtil {
	public static final Class[] WELL_KNOWN_VALUE_OBJECTS = {String.class, Date.class, BigDecimal.class};
	
	public static final BigDecimal ZERO_10 = new BigDecimal(BigInteger.ZERO, 10);
			
	/**
	 * if isSimpleProperty or sourceValue is null  - just sets it,
	 * if value is a collection or map - sets a copy of it  
	 */
	public static void updateProperty(BeanWrapper target, PropertyDescriptor p, Object sourceValue, boolean isSimpleProperty) {
		if (sourceValue == null || isSimpleProperty) {
			target.setPropertyValue(p.getName(), sourceValue);
		} else {			
			Object targetVal = target.getPropertyValue(p.getName());
			if (sourceValue instanceof Collection ) {
				Collection targetCol = (Collection)targetVal;
				targetCol = instantiateCollectionIfNull(target, p, sourceValue,	targetCol);
				targetCol.clear();
				targetCol.addAll((Collection)sourceValue);
			} else if (sourceValue instanceof Map) {
				Map targetMap = (Map)targetVal;
				targetMap = instantiateMapIfNull(target, p, targetMap);
				targetMap.clear();
				targetMap.putAll((Map)sourceValue);
			} else {
				throw new NotImplementedException("Collection type [" +sourceValue.getClass().getName() +"] is not supported");
			}			
		}
	}

	private static Map instantiateMapIfNull(BeanWrapper target,	PropertyDescriptor p, Map targetMap) {
		if (targetMap == null) {
			Map newMap = Maps.newHashMap();
			target.setPropertyValue(p.getName(), newMap);
		}
		return targetMap;
	}

	private static Collection instantiateCollectionIfNull(BeanWrapper target, PropertyDescriptor p, Object sourceValue, Collection targetCol) {
		if (targetCol == null) {
			Collection newCollection = CollectionUtil.newMutableCollectionInstance((Class<Collection>)sourceValue.getClass());
			target.setPropertyValue(p.getName(), newCollection);
			return newCollection;
		}
		return targetCol;
	}
			
	/**
	 * @return added values count
	 */
	public static int addMissingValuesToCollection(BeanWrapper target, PropertyDescriptor p, Collection sourceCollection) {
		Preconditions.checkNotNull(sourceCollection);
		
		Collection targetCollection = (Collection)target.getPropertyValue(p.getName());
		
		if (targetCollection == null) {
			targetCollection = CollectionUtil.newMutableCollectionInstance((Class<Collection>)sourceCollection.getClass());
			target.setPropertyValue(p.getName(), targetCollection);
		}
		
		Collection added = CollectionUtils.subtract(sourceCollection, targetCollection);
		targetCollection.addAll(added);
					
		return added.size();
	}
	
	/**
	 * adds entries which are only in sourceMap
	 */
	public static int addMissingValuesToSimpleMap(BeanWrapper target, PropertyDescriptor p, Map sourceMap) {
		Preconditions.checkNotNull(sourceMap);		
		Map targetMap = (Map)target.getPropertyValue(p.getName());
		
		if (targetMap == null) {
			targetMap = Maps.newHashMap();
			target.setPropertyValue(p.getName(), targetMap);
		}
		
		int added = 0;
		for (Object newKey: Maps.difference(sourceMap, targetMap).entriesOnlyOnLeft().keySet()) {
			Object newValue = sourceMap.get(newKey);
			targetMap.put(newKey, newValue);
			added++;
		}
		return added;
	}
	
	public static <K,V> int addMissingValuesToNestedMap(BeanWrapper target, PropertyDescriptor p, Map<K, Collection<V>> sourceNestedMap) {
		Preconditions.checkNotNull(sourceNestedMap);		
		Map<K, Collection<V>> targetNestedMap = (Map)target.getPropertyValue(p.getName());
		
		if (targetNestedMap == null) {
			targetNestedMap = Maps.newHashMap();
			target.setPropertyValue(p.getName(), targetNestedMap);
		}
		
		int added = 0;
		//new entries
		for (K newKey: Maps.difference(sourceNestedMap, targetNestedMap).entriesOnlyOnLeft().keySet()) {
			Collection<V> newSourceCol = sourceNestedMap.get(newKey);
			if (newSourceCol == null) {
				continue;
			}			
			targetNestedMap.put(newKey, CollectionUtil.copy(newSourceCol));
			added+=newSourceCol.size();
		}
		
		//changed entries
		for(K commonKey : (Collection<K>)CollectionUtils.intersection(sourceNestedMap.keySet(), targetNestedMap.keySet())){
			Collection<V> sourceCol = sourceNestedMap.get(commonKey);
			Collection<V> targetCol = targetNestedMap.get(commonKey);
			
			if (sourceCol == null) {
				continue;
			}			
			if (targetCol == null) {
				targetCol = CollectionUtil.newMutableCollectionInstance(sourceCol.getClass());				
			}
			
			Collection addedValues = CollectionUtils.subtract(sourceCol, targetCol);
			targetCol.addAll(addedValues);
			added+=addedValues.size();			
		}

		return added;
	}
	
	public static boolean isValueEmpty(PropertyDescriptor p, Object value) {
		if (value == null)
			return true;
		
		if (value instanceof String) {
			return StringUtils.isEmpty( (String)value );
		}
		
		if (value instanceof Collection<?>) {
			return CollectionUtils.isEmpty( (Collection<?>)value);
		}
		
		if (value instanceof Map) {
			return MapUtils.isEmpty( (Map)value );
		}
		
		if (isPrimitiveNumber(p)) {
			return isValuePrimiveZero(p, value);
		}
		
		return false;		
	}
	
	public static boolean isValuePrimiveZero(PropertyDescriptor p, Object value) {
		if (!isPrimitiveNumber(p) || value == null)
			return false;
		
		if (p.getPropertyType() == Integer.TYPE)
			return (Integer)value == 0;
		
		if (p.getPropertyType() == Long.TYPE)
			return (Long)value == 0;
		
		if (p.getPropertyType() == Double.TYPE || p.getPropertyType() == Float.TYPE) {
						
			BigDecimal big;
			if (p.getPropertyType() == Double.TYPE) {
				big = new BigDecimal((Double)value);
			}else {
				big = new BigDecimal((Float)value);
			}
			
			big = big.setScale(10, RoundingMode.HALF_UP);
			return ZERO_10.equals(big);
		}
		
		return false;
	}
	
	public static boolean isPrimitiveNumber(PropertyDescriptor p) {
		if (p.getPropertyType() == Short.TYPE || p.getPropertyType() == Byte.TYPE)
			throw new RuntimeException("short and byte are not supported");
			
		Set<Class> primitiveNumbers = new HashSet<Class>();
			 	primitiveNumbers.add(Integer.TYPE);
				primitiveNumbers.add(Long.TYPE);
				primitiveNumbers.add(Double.TYPE);
				primitiveNumbers.add(Float.TYPE);
		
		return primitiveNumbers.contains(p.getPropertyType());
	}
	
	public static boolean isPersistent(PropertyDescriptor p) {
		return ReflectionUtil.isPersistentGetter(p.getReadMethod());
	}
			
	public static boolean isWellKnownValueObject(PropertyDescriptor p) {
		return isWellKnownValueObject(p.getPropertyType());
	}
	
	/**
	 * true if property has one of those annotations
	 */
	public static boolean hasAnnotation(PropertyDescriptor p, Collection<Class<? extends Annotation>> annotationList) {
		for (Class ann : annotationList) {
			if (hasAnnotation(p, ann)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean hasAnnotation(PropertyDescriptor p, Class<? extends Annotation> annotationClass) {
		if (p.getReadMethod().isAnnotationPresent(annotationClass)) {
				return true;
		}
		return false;
	}
			
	/**
	 * isPrimitive or isPrimitiveBox or isWellKnownValueObject or isEnum or policy.isCustomValueObject(propertyType)
	 */
	public static boolean isSimpleProperty(PropertyDescriptor p, BeanOperationPolicy policy) {
		return isSimpleProperty(p.getPropertyType(), policy);
	}
	
	/**
	 * true jeśli nie jest kolekcją, mapą i simpleProperty
	 */
	public static boolean isReference(PropertyDescriptor p, BeanOperationPolicy policy) {
		return !isCollectionOrMap(p) && !isSimpleProperty(p, policy);
	}
	
	public static boolean isCollectionOfReferences(PropertyDescriptor p, BeanOperationPolicy policy) {
		if (!isCollection(p)) 
			return false;
		
		return !isCollectionContentSimple(p, policy);		
	}
	
	public static boolean isSimpleCollection(PropertyDescriptor p, BeanOperationPolicy policy) {
		if (!isCollection(p)) 
			return false;
		
		return isCollectionContentSimple(p, policy);			
	}
	
	public static boolean isSimpleMap(PropertyDescriptor p, BeanOperationPolicy policy) {
		if (!isMap(p)) 
			return false;
	
		return isMapContentSimple(p, policy);
	}
	
	/**
	 * true if  Map <SimpleProperty, Collection<SimpleProperty>>
	 */
	public static boolean isNestedMap(PropertyDescriptor p, BeanOperationPolicy policy) {
		if (!isMap(p)) 
			return false;
	
		return isMapContentSimpleToSimpleCollection(p, policy);
	}
	
	
	/*
	public static boolean isSimpleCollectionOrMap(PropertyDescriptor p, BeanMergePolicy policy) {
		if (!isCollectionOrMap(p)) 
			return false;
	
		if (isCollection(p)){
			return isCollectionContentSimpleProperty(p, policy);
		}
		if (isMap(p)){
			return isMapContentSimpleProperty(p, policy);
		}
		
		throw new RuntimeException();
	}*/
	
	public static String printProperty(BeanWrapper bean, PropertyDescriptor p, BeanMergePolicy policy) {
		
		String attrs=" ";
						
		if (isPersistent(p)) 
			attrs +="persistent ";

		if ( isEnum(p) )
			attrs += "enum ";
		
		if ( isPrimitive(p) )
			attrs += "primitive ";
		
		if ( isPrimitiveBox(p) )
			attrs += "primitiveBox ";
		
		if ( isWellKnownValueObject(p) )
			attrs += "ValueObject ";
		
		if ( policy != null) {
			if ( policy.isCustomValueObject(p) )
				attrs += "customValueObject ";
			
			if ( hasAnnotation(p, policy.getSkipAnnotations()))
				attrs += "skipped ";
		}
		
		String value = bean.getPropertyValue(p.getName())+"";
		
		String message = (StringUtils.rightPad(p.getName(),20))+"= "+
						 (StringUtils.rightPad(value,30))+
						 "[" +attrs+"] "+
						 ", type:" + p.getPropertyType();
		

		return message;
	} 
			
	//-- private
	
	private static boolean isEnum(Class propertyType) {
		return propertyType.isEnum();
	}

	private static boolean isCollection(Type propertyType) {
		if (propertyType instanceof Class ) {
			return Collection.class.isAssignableFrom((Class)propertyType);
		}
		if (propertyType instanceof ParameterizedType ) {
			Type rawType = ((ParameterizedType)propertyType).getRawType();
			return isCollection(rawType);
		}
		return false;
	}
	
	private static boolean isCollection(PropertyDescriptor p) {
		return isCollection(p.getPropertyType());
	}	
	
	private static boolean isMap(PropertyDescriptor p) {
		return Map.class.isAssignableFrom(p.getPropertyType());
	}	
	
	private static boolean isCollectionOrMap(PropertyDescriptor p) {
		return isCollection(p) || isMap(p);
	}	

	private static boolean isCollectionContentSimple(PropertyDescriptor p, BeanOperationPolicy policy) {
		Type contentType = ReflectionUtil.getReturnTypeParameter(p.getReadMethod());
		
		if (contentType == null) {
			throw new RuntimeException("type of collection "+p+" is not parametrized");
		}
		
		return isSimpleProperty(contentType, policy);		
	}
	
	private static boolean isMapContentSimpleToSimpleCollection(PropertyDescriptor p, BeanOperationPolicy policy) {
		Type[] contentTypes = ReflectionUtil.getReturnTypeParameters(p.getReadMethod());
		
		if (contentTypes.length != 2) {
			throw new RuntimeException("type of map "+p+" is not properly parametrized");
		}
		
		//System.out.println("contentTypes of : "+ p.getName());
		//System.out.println(".. contentTypes[0]: "+ contentTypes[0]);
		//System.out.println(".. contentTypes[1]: "+ contentTypes[1]);
		
		boolean ret =  isSimpleProperty(contentTypes[0], policy) &&
			           isCollection(contentTypes[1]) &&
			           isGenericType(contentTypes[1]) &&
			           isSimpleProperty(getTypeParameter(contentTypes[1]), policy);			           
				
		return ret;
	}
	
	private static boolean isMapContentSimple(PropertyDescriptor p, BeanOperationPolicy policy) {
		Type[] contentTypes = ReflectionUtil.getReturnTypeParameters(p.getReadMethod());
		
		if (contentTypes.length != 2) {
			throw new RuntimeException("type of map "+p+" is not properly parametrized");
		}
		
		return isSimpleProperty(contentTypes[0], policy) &&
			   isSimpleProperty(contentTypes[1], policy);

	}
	
	private static boolean isPrimitiveBox(Class propertyType) {
		return ReflectionUtil.isPrimitiveBox(propertyType);
	}
	
	private static boolean isPrimitiveBox(PropertyDescriptor p) {
		return isPrimitive(p.getPropertyType());
	}
	
	private static boolean isPrimitive(Class propertyType) {
		return propertyType.isPrimitive();
	}

	private static boolean isPrimitive(PropertyDescriptor p) {
		return isPrimitive(p.getPropertyType());
	}	
	
	private static boolean isSimpleProperty(Type propertyType, BeanOperationPolicy policy) {
		if (! (propertyType instanceof Class) ) {
			return false;
		}
		
		Class propertyClass = (Class)propertyType;
		
		return isPrimitive(propertyClass) || isPrimitiveBox(propertyClass) || isWellKnownValueObject(propertyClass) || isEnum(propertyClass) || policy.isCustomValueObject(propertyClass);
	}	
	
	private static boolean isEnum(PropertyDescriptor p) {
		return isEnum(p.getPropertyType());
	}	
	
	private static boolean isWellKnownValueObject(Class propertyType) {
		for (Class clazz: WELL_KNOWN_VALUE_OBJECTS) {
			if (clazz.isAssignableFrom(propertyType))
					return true;
		}
		return false;
	}
}
