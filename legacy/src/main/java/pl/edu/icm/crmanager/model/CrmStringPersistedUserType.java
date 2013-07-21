package pl.edu.icm.crmanager.model;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.hibernate.annotations.Type;
import org.hibernate.usertype.UserType;

import pl.edu.icm.sedno.common.hibernate.StringPersistedUserType;

/**
 * Used for value objects, persisted by Hibernate {@link UserType} in a single text column, 
 * for ex. OXM.
 * 
 * Given UserType has to implement {@link StringPersistedUserType}
 * 
 * @author bart
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface CrmStringPersistedUserType {
	/**
	 * fully qualified class name of {@link UserType}, the same as in {@link Type#type()}
	 */
	String type();
}
