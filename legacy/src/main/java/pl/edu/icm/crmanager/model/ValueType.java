package pl.edu.icm.crmanager.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import pl.edu.icm.crmanager.exception.CrmRuntimeException;
import pl.edu.icm.sedno.common.model.DataObject;
import pl.edu.icm.sedno.common.model.SednoDate;

/**
 * Value types supported by CRM
 * 
 * @author bart
 */
public enum ValueType { DECIMAL, 
	INT,
	BOOLEAN,
	DATE,
	REFERENCE,
	STRING,
	BIG_DECIMAL,
        LIST,
	/**
	 * simple String embeddable, for ex. {@link SednoDate}
	 */
	STRING_EMBEDDABLE,
	ENUM,
	/**
	 * TODO: discard? not sure it is needed, see {@link ChangeRequest#isComplexEmbeddable}
	 * see {@link CrmComplexEmbeddable}
	 */
	COMPLEX_STRING_EMBEDDABLE,
	/** 
	 * see {@link CrmStringPersistedUserType}
	 */
	STRING_PERSISTED_VALUE_OBJECT;
	
	/**
	 * static factory method
	 */
	public static ValueType determineValueType(Class valueClass) { 
		if (DataObject.class.isAssignableFrom( valueClass )) {
		   return ValueType.REFERENCE;
		}else if (Enum.class.isAssignableFrom( valueClass )) {
           return ValueType.ENUM; 
        } else if (valueClass.equals(String.class)) {
           return ValueType.STRING;
        } else if (valueClass.equals(Date.class)) {
            return ValueType.DATE;
        } else if (CrmSimpleEmbeddable.class.isAssignableFrom(valueClass)) {
            return ValueType.STRING_EMBEDDABLE;        
        } else if (CrmComplexEmbeddable.class.isAssignableFrom(valueClass)) {
            return ValueType.COMPLEX_STRING_EMBEDDABLE;                         
        } else if (valueClass.equals(Integer.TYPE) || valueClass.equals(Integer.class)) {
            return ValueType.INT;
        } else if (valueClass.equals(Double.TYPE) ||  valueClass.equals(Double.class)) {  
            return ValueType.DECIMAL;
        } else if (valueClass.equals(BigDecimal.class)) {  
            return ValueType.BIG_DECIMAL;            
        } else if (valueClass.equals(Boolean.TYPE) || valueClass.equals(Boolean.class)) {
            return ValueType.BOOLEAN;
        } else if (valueClass.isAnnotationPresent(CrmStringPersistedUserType.class)) {
        	return ValueType.STRING_PERSISTED_VALUE_OBJECT;
        } else if (List.class.isAssignableFrom(valueClass)) { 
                return ValueType.LIST;
        } else {
            throw new CrmRuntimeException("determineValueType(): "+ valueClass.getName() +" is not supported as value Class");
        }
    }
}