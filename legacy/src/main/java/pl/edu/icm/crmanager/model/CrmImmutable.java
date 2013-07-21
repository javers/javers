package pl.edu.icm.crmanager.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



/**
 * low level annotation, better choice:  {@link CrmTransparent}
 * 
 * property with this annotation can't be changed via CrmProxy
 * 
 * @author bart
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrmImmutable {

}
