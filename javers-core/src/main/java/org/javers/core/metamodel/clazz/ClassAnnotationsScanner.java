package org.javers.core.metamodel.clazz;

import org.javers.common.validation.Validate;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;
import static org.javers.common.collections.Sets.asSet;

/**
 * Should scan well known annotations at class level
 *
 * @author bartosz walacik
 */
public class ClassAnnotationsScanner {

    private final List<AnnotationNamesProvider> namesProviders;

    private final Set<String> entityAliases = new HashSet<>();
    private final Set<String> valueObjectAliases = new HashSet<>();
    private final Set<String> valueAliases = new HashSet<>();

    public ClassAnnotationsScanner(List<AnnotationNamesProvider> namesProviders) {
        this.namesProviders = namesProviders;

        for (AnnotationNamesProvider provider : namesProviders){
            entityAliases.addAll(provider.getEntityAlias());
            valueObjectAliases.addAll(provider.getValueObjectAlias());
            valueAliases.addAll(provider.getValueAlias());
        }
    }

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
