package org.javers.core.metamodel.annotation;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.ClientsClassDefinition;
import org.javers.core.metamodel.clazz.EntityDefinition;
import org.javers.core.metamodel.clazz.ValueDefinition;
import org.javers.core.metamodel.clazz.ValueObjectDefinition;

import java.lang.annotation.Annotation;

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

    public ClientsClassDefinition scanAndInfer(Class javaClass){
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
