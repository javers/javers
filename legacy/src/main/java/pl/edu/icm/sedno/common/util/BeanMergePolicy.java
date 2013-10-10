package pl.edu.icm.sedno.common.util;

import com.google.common.collect.Sets;
import pl.edu.icm.crmanager.model.CrmImmutable;
import pl.edu.icm.crmanager.model.CrmTransparent;

import javax.persistence.Id;
import javax.persistence.Version;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author bart
 */
public class BeanMergePolicy extends BeanOperationPolicy{
	public enum OverwritePolicy{	
		OVERWRITE,       //copy all source values
		UPDATE_IF_EMPTY, //copy if source is not empty and target is empty, in case of collections or maps: add all values from source that are missing in target
		SMART_OVERWRITE  //copy if source is not empty
	};
	
	public enum LogLevel{
		INFO,
		DEBUG,
		SILENT
	}
		
	//skip property if has one of those 
	private Set<Class<? extends Annotation>>  skipAnnotations;
	
	private OverwritePolicy overwritePolicy;

	//if true - skip transient properties
	private boolean persistentOnly;
		
	private LogLevel logLevel; 
	
	/**
	 * default policy 
	 */
	@SuppressWarnings("unchecked")
	public BeanMergePolicy() {	
		super();
		
		skipAnnotations = Sets.newHashSet(Version.class, 
										  Id.class,
				                          SkipOnMerge.class,   
				                          CrmImmutable.class, 
										  CrmTransparent.class);				                                   
				                                   
		persistentOnly = true;
		overwritePolicy = OverwritePolicy.UPDATE_IF_EMPTY;
		logLevel = LogLevel.DEBUG;
		
	}
	
	public static BeanMergePolicy defaultUpdateIfEmpty() {
		BeanMergePolicy p = new BeanMergePolicy();
		p.setOverwritePolicy(OverwritePolicy.UPDATE_IF_EMPTY);
		return p;
	}
	
	public static BeanMergePolicy defaultSmartOverwrite() {
		BeanMergePolicy p = new BeanMergePolicy();
		p.setOverwritePolicy(OverwritePolicy.SMART_OVERWRITE);
		return p;
	}
	
	public static BeanMergePolicy defaultOverwrite() {
		BeanMergePolicy p = new BeanMergePolicy();
		p.setOverwritePolicy(OverwritePolicy.OVERWRITE);
		return p;
	}
		
	//-- getters & setters
			
	public Set<Class<? extends Annotation>> getSkipAnnotations() {
		return skipAnnotations;
	}
		
	
	public boolean isPersistentOnly() {
		return persistentOnly;
	}
	
	public OverwritePolicy getOverwritePolicy() {
		return overwritePolicy;
	}
	
	public LogLevel getLogLevel() {
		return logLevel;
	}

	public void setOverwritePolicy(OverwritePolicy overwritePolicy) {
		this.overwritePolicy = overwritePolicy;
	}
	
	public void addSkipAnnotation(Class<? extends Annotation> skipAnnotation) {
		this.skipAnnotations.add(skipAnnotation);
	}	
	
	public void setPersistentOnly(boolean persistentOnly) {
		this.persistentOnly = persistentOnly;
	}
	
	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}
	
}
