package pl.edu.icm.sedno.common.util;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.base.BaseLocal;

import pl.edu.icm.sedno.common.model.SednoDate;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * @author bart
 */
public class BeanOperationPolicy {

	public  enum PropertySubset {		
		/**
		 * suitable in most simple cases
		 */
		ALL,
		/** 
		 * only if getter is declared in bean class (skips inherited properties),
		 * beware! not suitable for proxied beans 
		 */
		DECLARED_ONLY,
		/**
		 * only if getter is declared in class below ADataObject 
		 */
		BELOW_ADATA_OBJECT,		
		/**
		 * only if getter is declared in class below ADataObject, @ID getter is excluded 
		 */
		BELOW_ADATA_OBJECT_EXCLUDE_ID
	}

	
	private PropertySubset propertySubset;
	
	//custom ValueObject classes (ex. SednoDate)
	private Set<Class<? extends Object>>       valueClasses;
	
	/**
	 * Default policy
	 */
	public BeanOperationPolicy() {		
		propertySubset = PropertySubset.DECLARED_ONLY;
		valueClasses =   Sets.newHashSet(SednoDate.class, BaseLocal.class);
	}
	
	public BeanOperationPolicy(PropertySubset propertySubset, Set<Class<? extends Object>> valueClasses) {
		Preconditions.checkNotNull(propertySubset);
		this.propertySubset = propertySubset;
		this.valueClasses = valueClasses;
	}
	
	//--
	
	public boolean isCustomValueObject(Class propertyType) {
		for (Class clazz : valueClasses) {
			if (clazz.isAssignableFrom(propertyType))
				return true;
		}
		return false;
	}
	
	public boolean isCustomValueObject(PropertyDescriptor p) {
		return isCustomValueObject(p.getPropertyType());
	}
	
	public void addValueClass(Class valueClass) {
		getValueClasses().add(valueClass);
	}	

	public void addValueClasses(Collection valueClasses) {
		getValueClasses().addAll(valueClasses);
	}	
	
	//-- 
		
	public PropertySubset getPropertySubset() {
		return propertySubset;
	}
	
	private Set<Class<? extends Object>>  getValueClasses() {
		if (valueClasses == null) {
			valueClasses = new HashSet();
		}
		return valueClasses;
	}
}