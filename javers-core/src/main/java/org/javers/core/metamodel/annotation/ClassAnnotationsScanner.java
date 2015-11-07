package org.javers.core.metamodel.annotation;

import org.javers.common.collections.Optional;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.common.validation.Validate;
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

    public ClassAnnotationsScan scan(Class javaClass){
        Validate.argumentIsNotNull(javaClass);

        Optional<String> typeName = Optional.empty();
        for (Annotation ann : javaClass.getAnnotations()) {
            if (annotationNamesProvider.isTypeName(ann)) {
                typeName = Optional.of((String) ReflectionUtil.invokeGetter(ann, "value"));
            }
        }

        boolean hasValue = false;
        boolean hasValueObject = false;
        boolean hasEntity = false;

        //scan class level annotations
        for (Annotation ann : javaClass.getAnnotations()) {
            if (annotationNamesProvider.isEntityAlias(ann)) {
                hasEntity = true;
            }
            if (annotationNamesProvider.isValueAlias(ann)) {
                hasValue = true;
            }
            if (annotationNamesProvider.isValueObjectAlias(ann)) {
                hasValueObject = true;
            }
        }

        return new ClassAnnotationsScan(hasValue, hasValueObject, hasEntity, typeName);
    }
}
