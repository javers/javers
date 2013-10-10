package pl.edu.icm.crmanager.model;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import net.sf.ehcache.store.chm.ConcurrentHashMap;

import org.springframework.util.ObjectUtils;

import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.crmanager.exception.UnsupportedMapping;
import pl.edu.icm.crmanager.logic.BCodeGeneratorImpl;
import pl.edu.icm.crmanager.logic.MethodType;
import pl.edu.icm.sedno.common.hibernate.StringPersistedUserType;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.common.util.ReflectionUtil;

import com.google.common.primitives.Primitives;

/**
 * @author bart
 */
public class ChangeRequestFactory {
	private static ConcurrentMap<Class, StringPersistedUserType> userTypes = new ConcurrentHashMap<Class, StringPersistedUserType>();
	
	public static ChangeRequest createForDeletedObject(DataObject node) {
		return new ChangeRequest(node, null, RecType.DELETED, null);		
	}
	
	public static ChangeRequest createForNewObject(DataObject node) {
		ChangeRequest rec = new ChangeRequest(node, null, RecType.NEW_OBJECT, null);		
		return rec;
	}
	
	public static ChangeRequest createChildRemove(DataObject node, String getterName, DataObject child) {
		 ChangeRequest rec = new ChangeRequest(node, getterName, RecType.CHILD_REMOVE, ValueType.REFERENCE);		    
	     rec.setOldReferenceCombo( child );        	        
	     
	     return rec;
	}
	
	public static ChangeRequest createChildAdd(DataObject node, String getterName, DataObject child) {
		 ChangeRequest rec = new ChangeRequest(node, getterName, RecType.CHILD_ADD, ValueType.REFERENCE);		    
		 rec.setNewReferenceCombo(child);     	        
	     
	     return rec;
	}
	
	public static ChangeRequest createChange(DataObject modPoint, String getterName, Object oldValue, Object newValue, Class valueClass) {
		
		 ValueType valueType = ValueType.determineValueType(valueClass);		
		
		 if (valueType == ValueType.REFERENCE) {
			 return createReferenceChange(modPoint, getterName, oldValue, newValue);
	     } else {
			 return createValueChange(modPoint, getterName, oldValue, newValue, valueClass);
		 }
	}
		
	public static List<ChangeRequest> detectChangesOnComplexEmbeddable(DataObject node, String superGetterName, Object oldEmbeddableObj, Object newEmbeddableObj, Class superValueClass) {
	    List<ChangeRequest> changes = new ArrayList<ChangeRequest>();
	    
	    for (Method getter : ReflectionUtil.getPersistentGetters(superValueClass) ) {
	        MethodType mType = BCodeGeneratorImpl.determineMethodType(getter);
	        
	        if (mType != MethodType.simpleValue)
	            throw new UnsupportedMapping("getter ["+getter+"] in @Embeddable class is not a simpleValue");
	        
	        
	        Object oldValue = null;
	        Object newValue = null;
	        
	        if (oldEmbeddableObj != null) oldValue = ReflectionUtil.invokeGetter(getter, oldEmbeddableObj);
	        if (newEmbeddableObj != null) newValue = ReflectionUtil.invokeGetter(getter, newEmbeddableObj);

	        //logger.info("detectChangesOn: "+ getter);
	        if (!ObjectUtils.nullSafeEquals(oldValue, newValue)) {
	           changes.add( createChange (node, superGetterName+"."+getter.getName(), oldValue, newValue, getter.getReturnType()));
	        }
	            
	    }
	    
	    return changes;
	}
	
	private static ChangeRequest createReferenceChange(DataObject modPoint, String getterName, Object oldValue, Object newValue) {
		ChangeRequest rec = new ChangeRequest(modPoint, getterName, RecType.REFERENCE_CHANGE, ValueType.REFERENCE);
	    
        if (oldValue != null) {
            rec.setOldReferenceCombo((DataObject)oldValue);
        }
        if (newValue != null) {
            rec.setNewReferenceCombo((DataObject)newValue);
        }
        
        return rec;
	}
	
	private static ChangeRequest createValueChange(DataObject modPoint, String getterName, Object oldValue, Object newValue, Class valueClass) {
		ValueType valueType = ValueType.determineValueType(valueClass);
		
		ChangeRequest rec = new ChangeRequest(modPoint, getterName, RecType.VALUE_CHANGE, valueType);
		
	    if (valueType == ValueType.STRING) {
            rec.setOldStringValue ((String)oldValue);
            rec.setNewStringValue ((String)newValue);
        } else if (valueType == ValueType.STRING_EMBEDDABLE) {
            if (oldValue != null)
                rec.setOldStringValue ( ((CrmSimpleEmbeddable)oldValue).getDatabaseValue());
            if (newValue != null)
                rec.setNewStringValue ( ((CrmSimpleEmbeddable)newValue).getDatabaseValue());
        } else if (valueType == ValueType.ENUM) {
            if (oldValue != null)
                rec.setOldStringValue ( ((Enum)oldValue).name());
            if (newValue != null)
                rec.setNewStringValue ( ((Enum)newValue).name());
        } else if (valueType == ValueType.DATE) {
            rec.setOldDateValue  ((Date)oldValue);
            rec.setNewDateValue ((Date)newValue);
        } else if (valueType == ValueType.INT) {
            rec.setOldIntValue  ((Integer)oldValue);
            rec.setNewIntValue ((Integer)newValue);
        } else if (valueType == ValueType.DECIMAL) {
            rec.setOldDecimalValue  ((Double)oldValue);
            rec.setNewDecimalValue  ((Double)newValue);    
        } else if (valueType == ValueType.BIG_DECIMAL) {
            rec.setOldBigDecimalValue  ((BigDecimal)oldValue);
            rec.setNewBigDecimalValue  ((BigDecimal)newValue);                                  
        } else if (valueType == ValueType.BOOLEAN) {
            rec.setOldBooleanValue  ((Boolean)oldValue);
            rec.setNewBooleanValue  ((Boolean)newValue);
        } else if (valueType == ValueType.STRING_PERSISTED_VALUE_OBJECT) {
        	StringPersistedUserType userType = getUserType(valueClass);
        	rec.setOldStringValue( userType.writeToDatabase(oldValue) );
        	rec.setNewStringValue( userType.writeToDatabase(newValue) );
        } else if(valueType == ValueType.LIST) {
                Iterator itOld = ((List)oldValue).iterator();
                Iterator itNew = ((List)newValue).iterator();
                Object oldObj = null;
                Object newObj = null;
                do {
                    oldObj = itOld.hasNext() ? itOld.next() : null;
                    newObj = itNew.hasNext() ? itNew.next() : null;
                    if((oldObj!=null && !(Primitives.isWrapperType(oldObj.getClass()) || oldObj.getClass().equals(String.class))) ||
                       (newObj!=null && !(Primitives.isWrapperType(newObj.getClass()) || newObj.getClass().equals(String.class)))) {
                        throw new CrmRuntimeException("valueType: "+valueType+" is supported only if all elements are primitives or string");
                    }
                } while(oldObj!=null && newObj!=null);
                
                rec.setOldPrimitiveListValue((List)oldValue);
                rec.setNewPrimitiveListValue((List)newValue);
        } else {
        	throw new CrmRuntimeException("valueType: " + valueType + " not supported");
        }
	    
	    return rec;
	}	
	
	
	/**
	 * cached
	 */
	public static StringPersistedUserType getUserType(Class<?> valueClass) {
		if (!userTypes.containsKey(valueClass)) {
			synchronized (ChangeRequestFactory.class) {
				CrmStringPersistedUserType ann = valueClass.getAnnotation(CrmStringPersistedUserType.class);
				
				Class userTypeClass;
				try {
					userTypeClass = Class.forName(ann.type());
				} catch (ClassNotFoundException e) {
					return null;
				}
				
				if (!StringPersistedUserType.class.isAssignableFrom(userTypeClass)) {
					throw new CrmRuntimeException("Class "+ann.type()+" has to implement StringPersistedUserType interface");
				}
				
				StringPersistedUserType userType = (StringPersistedUserType) ReflectionUtil.invokeConstructor(userTypeClass);
				
				userTypes.put(valueClass, userType);
			}			
		}
		
		return userTypes.get(valueClass);
	}
	
}
