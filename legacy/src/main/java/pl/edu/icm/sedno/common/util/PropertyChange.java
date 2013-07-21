package pl.edu.icm.sedno.common.util;

import java.io.Serializable;
import java.lang.reflect.Method;

import pl.edu.icm.crmanager.model.RecType;

import com.google.common.base.Objects;

/**
 * DTO used by {@link BeanUtil#diff()}
 * 
 * @author bart
 */
public class PropertyChange implements Serializable{
	
	//private transient Method  modPointGetter;
	private String  modPointGetterName;
	private String  modPointClassName;
	private Object  modPoint;
	private RecType recType;
	private Object  oldValue;
	private Object  newValue;
		
	public PropertyChange(Method modPointGetter, Object modPoint, RecType recType, Object oldValue, Object newValue) {
		//this.modPointGetter = modPointGetter;
		this.modPointGetterName = modPointGetter.getName();
		this.modPointClassName =  modPointGetter.getDeclaringClass().getName();
		this.modPoint = modPoint;
		this.recType = recType;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	/**
	 * changed object
	 */
	public Object getModPoint() {
		return modPoint;
	}
		
	/**
	 * modPointGetter name
	 */
	public String getModPointGetterName() {
		return modPointGetterName;
	}
	
	/**
	 * modPointGetter declaring class
	 */
	public String getModPointClassName() {
		return modPointClassName;
	}
	
	/**
	 * CRM change type
	 */
	public RecType getRecType() {
		return recType;
	}
	
	public Object getOldValue() {
		return oldValue;
	}
	
	public Object getNewValue() {
		return newValue;
	}
	
    @Override
	public String toString() {
	    	return Objects.toStringHelper(PropertyChange.class)    			 	    			
	 	    	   .add("modPointGetter", modPointGetterName+"()")
	 	    	   .add("recType", recType)
	 	    	   .add("oldValue", oldValue)
	 	    	   .add("newValue", newValue)
	 	    	   .add("modPoint",  modPoint)	 	    	   
	 	    	   .toString();
    }
}
