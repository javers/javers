package pl.edu.icm.crmanager.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * zmiany wykonywane na property z tą annotacją nie są rejestrowane w crm
 * 
 * @author bart
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CrmTransparent {

}
