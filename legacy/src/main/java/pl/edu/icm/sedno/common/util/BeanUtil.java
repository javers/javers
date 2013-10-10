package pl.edu.icm.sedno.common.util;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.ObjectUtils;
import pl.edu.icm.crmanager.model.RecType;
import pl.edu.icm.sedno.common.model.ADataObject;
import pl.edu.icm.sedno.common.util.BeanMergePolicy.LogLevel;
import pl.edu.icm.sedno.common.util.BeanMergePolicy.OverwritePolicy;
import pl.edu.icm.sedno.common.util.BeanOperationPolicy.PropertySubset;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkNotNull;
import static pl.edu.icm.sedno.common.util.BeanPropertyUtil.*;


/**
 * metody equals, merge i diff dla bean'ów
 * 
 * @author bart
 */
public class BeanUtil {
	private static Logger logger = LoggerFactory.getLogger(BeanUtil.class);
			
	/**
	 * <pre>
	 * Tool for copying properties from one bean to another, 
	 * Following property types are supported:
	 *  - {@link BeanPropertyUtil#isSimpleProperty()}
	 *  - {@link BeanPropertyUtil#isSimpleCollection()}
	 *  - {@link BeanPropertyUtil#isSimpleMap()}
	 *  - {@link BeanPropertyUtil#isNestedMap()}
	 * <br/>
	 * 
	 * Simple property are:
	 *  - primitives and boxes
	 *  - enums
	 *  - Strings
	 *  - well known ValueObjects like java.util.Date, BigDecimal
	 *  - objects specified on custom ValueObject list - like SednoDate
	 * 
	 * Skipped properties:
	 *  - others objects, for ex. references to entities
	 *  - without public getter    
	 * </ul>
	 * 
	 * </pre>
	 * 
	 * @see BeanMergePolicy
	 * @return no of merged properties
	 */
	public static int mergeProperties(Object source, Object target, BeanMergePolicy policy) {
		checkNotNull(source, "mergeProperties(): source is null");
		checkNotNull(target, "mergeProperties(): target is null");
		checkNotNull(policy, "mergeProperties(): policy is null");
		
		BeanWrapper sourceBean = new BeanWrapperImpl(source);
		BeanWrapper targetBean = new BeanWrapperImpl(target);
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("start mergeProperties()\nsource: "+getObjectInfo(source)+"\ntarget: "+getObjectInfo(target) +"\npolicy: "+policy.getOverwritePolicy()+"\n");
		
		int mergedProperties = 0;
		for (PropertyDescriptor p: sourceBean.getPropertyDescriptors()) {
			if (policy.isPersistentOnly() && !isPersistent(p)) continue;
			
			if (hasAnnotation(p, policy.getSkipAnnotations())) continue;
			
			boolean merged = false;
			if (isSimpleProperty(p, policy) || 
				isSimpleCollection(p, policy) ||
				isSimpleMap(p, policy) ||
				isNestedMap(p, policy)) {
				merged = doMergeSingleProperty(p, sourceBean, targetBean, policy);
						
				buf.append(".. merging property (doCopy="+merged+") : "+printProperty(sourceBean, p, policy) +"\n");
			}
			if (merged) {
				mergedProperties ++;
			}
		}
		buf.append("copied values: "+ mergedProperties);
		
		log (buf.toString(),policy);
		
		return mergedProperties;
	}
		
	/**
	 * true if beans have equal properties,
	 * properties are compared using nullSafe equals - {@link Objects#equal()}
	 * 
	 * skips @Transient and private properties
	 */
	public static boolean equals(Object bean1, Object bean2, PropertySubset subset, String[] skipProperties) {		
		checkNotNull(bean1, "mergeProperties(): bean1 is null");
		checkNotNull(bean2, "mergeProperties(): bean2 is null");
		checkNotNull(subset, "mergeProperties(): subset is null");
		
		logger.trace("equals ( bean1:"+bean1+", bean2:"+ bean2 +" )"); //", subset:"+subset+", skipProperties:"+StringUtils.join(skipProperties," ") +" )");
		
	    if (bean1 == bean2)  
	    	return true;  
		
		BeanWrapper wrappedBean1 = new BeanWrapperImpl(bean1);
		BeanWrapper wrappedBean2 = new BeanWrapperImpl(bean2);
		
		checkProperties(wrappedBean1, wrappedBean2);
		
		Set<String> skipProperties_ = Sets.newHashSet(skipProperties);
		for (PropertyDescriptor p: wrappedBean1.getPropertyDescriptors()) {

			if (isSkippedTransientOrNotInSubset(p, bean1.getClass(), subset, skipProperties_)) {
				continue;
			}			
								
			Object value1 = wrappedBean1.getPropertyValue(p.getName());
			Object value2 = wrappedBean2.getPropertyValue(p.getName());
			
			logger.trace(".. comparing ["+ p.getReadMethod().getDeclaringClass().getSimpleName()+"."+ p.getName()+"] v1:"+value1+", v2:"+value2);
			if (!Objects.equal(value1, value2)) {
				//logger.debug("equals == false");
				return false;
			}
		}
		
		logger.trace("equals == true");
		return true;
	}

	/**
	 * skrót
	 * @return equals(bean1, bean2, subset, new String[]{})
	 */
	public static boolean equals(Object bean1, Object bean2, PropertySubset subset) {	
		return equals(bean1, bean2, subset, new String[]{});
	}
	
	/**
	 * <pre>
	 * shallow diff tool,
	 * properties are compared using nullSafe equals - {@link Objects#equal()}
	 * 
	 * following property types are supported:
	 *  - {@link BeanPropertyUtil#isSimpleProperty()}
	 *  - {@link BeanPropertyUtil#isSimpleMap()}
	 *  - {@link BeanPropertyUtil#isNestedMap()}
	 *  - {@link BeanPropertyUtil#isSimpleCollection()}
	 *  - {@link BeanPropertyUtil#isReference()}
	 *  - {@link BeanPropertyUtil#isCollectionOfReferences()}
	 * 
	 * skips @Transient, @SkipOnMerge and private properties
	 * 
	 * </pre>
	 * 
	 * @throws RuntimeException if found not supported property type
	 */
	public static List<PropertyChange> diff(Object oldVersion, Object newVersion, BeanOperationPolicy diffPolicy) {
		
		checkNotNull(oldVersion, "diff(): oldVersion is null");
		checkNotNull(newVersion, "diff(): newVersion is null");
		checkNotNull(diffPolicy, "mergeProperties(): diffPolicy is null");
		
		logger.debug("diff ( oldVersion:"+oldVersion+", newVersion:"+ newVersion );
		
	    if (oldVersion == newVersion)  {
	    	return Collections.EMPTY_LIST;
	    }
		
		BeanWrapper wrappedOld = new BeanWrapperImpl(oldVersion);
		BeanWrapper wrappedNew = new BeanWrapperImpl(newVersion);
		
		checkProperties(wrappedOld, wrappedNew);

		List<PropertyChange> changes = Lists.newArrayList();
		for (PropertyDescriptor p: wrappedOld.getPropertyDescriptors()) {

			if (isSkippedTransientOrNotInSubset(p, oldVersion.getClass(), diffPolicy.getPropertySubset(), Collections.EMPTY_SET)) {
				continue;
			}	

			if (hasAnnotation(p, SkipOnMerge.class)) {
				continue;
			}
			
			Object oldValue = wrappedOld.getPropertyValue(p.getName());
			Object newValue = wrappedNew.getPropertyValue(p.getName());
			
			//logger.debug(".. comparing ["+ p.getReadMethod().getDeclaringClass().getSimpleName()+"."+ p.getName()+"] v1:"+oldValue+", v2:"+newValue);
			
			if (Objects.equal(oldValue, newValue)) {
				continue;
			}
			
			//just compare fields, no drill down
			
			if (isSimpleProperty(p, diffPolicy)) {
				changes.add ( createChangeOnSimpleProperty(newVersion, p, oldValue, newValue ));
			}
			else if (isSimpleMap(p, diffPolicy)) {
				changes.addAll ( doDiffOnSimpleMaps(newVersion, p.getReadMethod(), (Map)oldValue, (Map)newValue ));
			}
			else if (isNestedMap(p, diffPolicy)) {
				List oldFlatten = CollectionUtil.flattenNestedMap((Map)oldValue);
				List newFlatten = CollectionUtil.flattenNestedMap((Map)newValue);
				changes.addAll ( doDiffOnSimpleCollections(newVersion, p.getReadMethod(), oldFlatten, newFlatten ));
			}
			else if (isSimpleCollection(p, diffPolicy)) {
				changes.addAll ( doDiffOnSimpleCollections(newVersion, p.getReadMethod(), (Collection)oldValue, (Collection)newValue ));
			}			
			else if (isReference(p, diffPolicy)) {
				changes.add ( createChangeOnReference(newVersion, p, oldValue, newValue ));
			}		
			else if (isCollectionOfReferences(p, diffPolicy)) {
				changes.addAll ( doDiffOnRefCollections(newVersion, p.getReadMethod(), (Collection)oldValue, (Collection)newValue ));
			}	
			else {
				throw new RuntimeException("diff() : no idea what it is - "+ p.getReadMethod());
			}
		}
		
		return changes;
	}

	public static  List<PropertyChange> doDiffOnSimpleMaps(Object modPoint, Method modPointGetter, Map oldMap, Map newMap) {
		oldMap = CollectionUtil.wrapNullToImmutableEmptyMap(oldMap);
		newMap = CollectionUtil.wrapNullToImmutableEmptyMap(newMap);

		List<PropertyChange> changes = Lists.newArrayList();
		
		MapDifference<Object, Object> mDiff = Maps.difference(oldMap, newMap);
		
		//detect value changes -
		//keys that appear in both maps, but with different values
		for (Entry<Object, ValueDifference<Object>> valueDiffEntry : mDiff.entriesDiffering().entrySet()) {
			Object key = valueDiffEntry.getKey();
			ValueDifference<Object> vDiff = valueDiffEntry.getValue();
			
			Entry oldEntry = new SimpleEntry(key, vDiff.leftValue());
			Entry newEntry = new SimpleEntry(key, vDiff.rightValue());
			
			changes.add( new PropertyChange(modPointGetter, modPoint, RecType.VALUE_CHANGE, oldEntry, newEntry) );
		}		
				
		//entries removed
		for (Entry<Object, Object> removedEntry : mDiff.entriesOnlyOnLeft().entrySet()) {
			//new SimpleEntry because we dont want: java.util.Collections$UnmodifiableMap$UnmodifiableEntrySet$UnmodifiableEntry,
			//which is not serializable
			changes.add( new PropertyChange(modPointGetter, modPoint, RecType.VALUE_REMOVE, new SimpleEntry(removedEntry), null) );
		}
		
		//entries added
		for (Entry<Object, Object> addedEntry : mDiff.entriesOnlyOnRight().entrySet()) {						
			changes.add( new PropertyChange(modPointGetter, modPoint, RecType.VALUE_ADD, null, new SimpleEntry(addedEntry)) );
		}
		
		return changes;
	}
	
	public static  List<PropertyChange> doDiffOnSimpleCollections(Object modPoint, Method modPointGetter, Collection oldCol, Collection newCol) {
		return doDiffOnCollections__(modPoint, modPointGetter, oldCol, newCol, true);
	}
	
	public static  List<PropertyChange> doDiffOnRefCollections(Object modPoint, Method modPointGetter, Collection oldCol, Collection newCol) {
		return doDiffOnCollections__(modPoint, modPointGetter, oldCol, newCol, false);
	}
		
	private static List<PropertyChange> doDiffOnCollections__(Object modPoint, Method modPointGetter, Collection oldCol, Collection newCol, boolean isSimpleCollection) {
		oldCol = CollectionUtil.wrapNullToImmutableCol(oldCol, (Class<Collection>)modPointGetter.getReturnType());
		newCol = CollectionUtil.wrapNullToImmutableCol(newCol, (Class<Collection>)modPointGetter.getReturnType());
									
        if ( oldCol.equals(newCol)) {
            return Collections.EMPTY_LIST;
        }

        RecType elementAddType =    RecType.CHILD_ADD;
        RecType elementRemoveType = RecType.CHILD_REMOVE;
        
        if (isSimpleCollection) {
            elementAddType =    RecType.VALUE_ADD;
            elementRemoveType = RecType.VALUE_REMOVE;
        }
        
        Collection added =   CollectionUtils.subtract(newCol, oldCol);
        Collection removed = CollectionUtils.subtract(oldCol, newCol);        
        
        List<PropertyChange> changes = Lists.newArrayList();
                
        for (Object child : removed) {
        	changes.add( new PropertyChange(modPointGetter, modPoint, elementRemoveType, child, null));
        }
        
        for (Object child : added) {
        	changes.add( new PropertyChange(modPointGetter, modPoint, elementAddType, null, child));
        }
       
		return changes;	
	}
	
	private static PropertyChange createChangeOnReference(Object modPoint, PropertyDescriptor property, Object oldRef, Object newRef) {
		return new PropertyChange(property.getReadMethod(), modPoint, RecType.REFERENCE_CHANGE, oldRef, newRef);
	}
	
	private static PropertyChange createChangeOnSimpleProperty(Object modPoint, PropertyDescriptor property, Object oldValue, Object newValue) {
		return new PropertyChange(property.getReadMethod(), modPoint, RecType.VALUE_CHANGE, oldValue, newValue);
	}
	
	public static void printPersistentProperties(Object bean, BeanMergePolicy policy) {
		 BeanWrapper wrapper = new BeanWrapperImpl(bean);
			
		 logger.info("persistent properties of bean "+ bean.toString());
		 for (PropertyDescriptor p: wrapper.getPropertyDescriptors()) {
			 if (!isPersistent(p)) {
				 continue;
			 }
			 
			 logger.info(".. "+printProperty(wrapper, p, policy));
		 }
	}
	
	private static boolean isSkippedTransientOrNotInSubset(PropertyDescriptor p, Class beanClass, PropertySubset subset, Set<String> skipProperties) {
		if (skipProperties.contains(p.getName())) {
			return true;
		}			
		
		if (p.getReadMethod().isAnnotationPresent(Transient.class)) {
			return true;
		}
		
		if (!isPropertyInSubset(p, subset, beanClass)) {
			return true;
		}
		
		return false;
	}
	
	private static void checkProperties(BeanWrapper wrappedBean1, BeanWrapper wrappedBean2) {
		if (wrappedBean1.getPropertyDescriptors().length !=  wrappedBean2.getPropertyDescriptors().length) {
			throw new IllegalArgumentException("equals(): looks like args can't be compared");
		}
	}	
	
	private static boolean isPropertyInSubset(PropertyDescriptor p, PropertySubset subset, Class beanClass) {
		if (subset == PropertySubset.ALL) {
		    return true;	
		} else if (subset == PropertySubset.DECLARED_ONLY) {
			return p.getReadMethod().getDeclaringClass() == beanClass;
		} else if (subset == PropertySubset.BELOW_ADATA_OBJECT) {
			return isDeclaredBelowADataObject(p);
		} else if (subset == PropertySubset.BELOW_ADATA_OBJECT_EXCLUDE_ID) {
			return isDeclaredBelowADataObject(p) && !p.getReadMethod().isAnnotationPresent(Id.class);
		} else {
			throw new NotImplementedException("subset: " + subset +" is not supported");
		}
	}
	
	private static boolean isDeclaredBelowADataObject(PropertyDescriptor p) {
		return ADataObject.class.isAssignableFrom(p.getReadMethod().getDeclaringClass()) && 
			   ADataObject.class != p.getReadMethod().getDeclaringClass();
	}
	
	/**
	 * @return true if merged, 0 otherwise
	 */
	private static boolean doMergeSingleProperty(PropertyDescriptor p, BeanWrapper source, BeanWrapper target, BeanMergePolicy policy) {
		boolean merged = false;
		
		boolean isSimpleProperty = isSimpleProperty(p, policy);
		
		Object sValue = source.getPropertyValue(p.getName());
		Object tValue = target.getPropertyValue(p.getName());
		
		if (ObjectUtils.nullSafeEquals(sValue, tValue)) {
			return false;
		}		
		
		//OVERWRITE
		if (policy.getOverwritePolicy() == OverwritePolicy.OVERWRITE) {			
			updateProperty(target, p, sValue, isSimpleProperty);
			merged = true;
		} 
		//UPDATE_IF_EMPTY - for simple property
		else if (policy.getOverwritePolicy() == OverwritePolicy.UPDATE_IF_EMPTY && isSimpleProperty) {
			if (isValueEmpty(p, tValue) && !isValueEmpty(p, sValue)) {
				updateProperty(target, p, sValue, isSimpleProperty);
				merged = true;
			}			
		} 
		//UPDATE_IF_EMPTY - for collection or map, empty source
		else if (policy.getOverwritePolicy() == OverwritePolicy.UPDATE_IF_EMPTY && !isSimpleProperty && isValueEmpty(p, sValue)) {
			//do nothing
		}
		//UPDATE_IF_EMPTY - for collection or map
		else if (policy.getOverwritePolicy() == OverwritePolicy.UPDATE_IF_EMPTY && !isSimpleProperty && !isValueEmpty(p, sValue)) {
			int added = 0;
			if (isSimpleCollection(p, policy)) {
				added = addMissingValuesToCollection(target, p, (Collection)sValue);	
			}else if (isSimpleMap(p, policy)) {
				added = addMissingValuesToSimpleMap(target, p, (Map)sValue);				
			}else if (isNestedMap(p, policy)) {
				added = addMissingValuesToNestedMap(target, p, (Map)sValue);
			}			
			else {
				throw new NotImplementedException("Collection type [" +sValue.getClass().getName() +"] is not supported");
			}
			merged = (added>0);
		} 
		//SMART_OVERWRITE
		else if (policy.getOverwritePolicy() == OverwritePolicy.SMART_OVERWRITE) {			
			if (!isValueEmpty(p, sValue)) {
				updateProperty(target, p, sValue, isSimpleProperty);
				merged = true;
			}
		} else {
			throw new NotImplementedException ("policy "+policy.getOverwritePolicy()+ " is not implementd");
		}

		return merged;
	}
	
    private static String getObjectInfo(Object o) {
    	if (o instanceof ADataObject)
    		return ((ADataObject)o).getGlobalId();
    	
    	return o.toString();
    }
	
	private static void log(String text, BeanMergePolicy policy) {
		if (policy.getLogLevel() == LogLevel.DEBUG) {
			logger.debug(text);
		}
		
		if (policy.getLogLevel() == LogLevel.INFO) {
			logger.info(text);
		}
	}	        
			
	/*
	public static boolean isString(PropertyDescriptor p) {
		return String.class.isAssignableFrom( p.getPropertyType() );
	}*/			
}
