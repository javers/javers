package pl.edu.icm.crmanager.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotacja używana do określenia miejsc w modelu danych, których zmiana jest 'istotna'.
 * 
 * Zawiera referencje do klasy Votera, króry implementuje logikę klasyfikacji zmian.
 * 
 * @see ChangeVoter
 * @author bart
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChangeImportantVoter {
	Class<? extends ChangeVoter<?,?>> voterClass();
}
