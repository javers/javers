package pl.edu.icm.crmanager.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotacja używana do określenia miejsc w modelu danych, których zmiana jest 'istotna'.
 * Informacja o istotności zmiany może być ważna dla klienta CRM, np. do podjęcia decyzji czy wykonywać auto-akceptację 
 *  
 * @author bart
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChangeImportant {

}
