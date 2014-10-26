package org.javers.core.metamodel.clazz;

import org.javers.common.validation.Validate;

import java.lang.annotation.Annotation;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static org.javers.common.collections.Sets.asSet;

/**
 * Should scan well known annotations at class level
 *
 * @author bartosz walacik
 */
public class ClassAnnotationsScanner {

    private final Set<String> entityAliases =
            unmodifiableSet(asSet("Entity", "MappedSuperclass"));

    private final Set<String> valueObjectAliases =
            unmodifiableSet(asSet("ValueObject", "Embeddable"));

    private final Set<String> valueAliases =
            unmodifiableSet(asSet("Value"));

    ClientsClassDefinition scanAndInfer(Class javaClass){
        Validate.argumentIsNotNull(javaClass);

        for (Annotation ann : javaClass.getAnnotations()){
            if (entityAliases.contains( ann.annotationType().getSimpleName() )) {
                return new EntityDefinition(javaClass);
            }

            if (valueObjectAliases.contains( ann.annotationType().getSimpleName() )) {
                return new ValueObjectDefinition(javaClass);
            }

            if (valueAliases.contains( ann.annotationType().getSimpleName() )) {
                return new ValueDefinition(javaClass);
            }
        }

        return new ValueObjectDefinition(javaClass);
    }
}
