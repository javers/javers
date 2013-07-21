package pl.edu.icm.crmanager.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotacja używana do nawigowania po drzewie obiektów, np w diff.
 *
 * Nadawana dla persystentnych getterów. Definiuje relację parent-child w drzewie.
 * 
 * Jeśli obiekt jest węzłem w drzewie, to obiekt/obiekty z annotacją @Component
 * są jego child'ami.
 * 
 * Operacje wykonywane (na parent node) będą propagowane na child nodes.
 * 
 * @author bart
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface Component {
}
