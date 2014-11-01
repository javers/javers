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

    private final AnnotationNamesProvider annotationNamesProvider;

    public ClassAnnotationsScanner(AnnotationNamesProvider annotationNamesProvider) {
        this.annotationNamesProvider = annotationNamesProvider;
    }

    ClientsClassDefinition scanAndInfer(Class javaClass){
        Validate.argumentIsNotNull(javaClass);

        for (Annotation ann : javaClass.getAnnotations()){
            if (annotationNamesProvider.isEntityAlias(ann)) {
                return new EntityDefinition(javaClass);
            }

            if (annotationNamesProvider.isValueObjectAlias(ann)) {
                return new ValueObjectDefinition(javaClass);
            }

            if (annotationNamesProvider.isValueAlias(ann)) {
                return new ValueDefinition(javaClass);
            }
        }

        return new ValueObjectDefinition(javaClass);
    }
}
