package org.javers.core.metamodel.scanner;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.common.validation.Validate;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * Should scan well known annotations at class level
 *
 * @author bartosz walacik
 */
class ClassAnnotationsScanner {

    private final AnnotationNamesProvider annotationNamesProvider;

    ClassAnnotationsScanner(AnnotationNamesProvider annotationNamesProvider) {
        this.annotationNamesProvider = annotationNamesProvider;
    }

    public ClassAnnotationsScan scan(Class javaClass){
        Validate.argumentIsNotNull(javaClass);

        Optional<String> typeName = ReflectionUtil.findFirst(javaClass, annotationNamesProvider.getTypeNameAliases())
                .map(a -> ReflectionUtil.getAnnotationValue(a, "value"));

        boolean hasValue = false;
        boolean hasIgnored = false;
        boolean hasValueObject = false;
        boolean hasEntity = false;
        boolean hasShallowReference = false;
        boolean hasIgnoreDeclaredProperties = false;

        //scan class level annotations
        for (Annotation ann : javaClass.getAnnotations()) {
            if (annotationNamesProvider.isEntityAlias(ann)) {
                hasEntity = true;
            }
            if (annotationNamesProvider.isValueAlias(ann)) {
                hasValue = true;
            }

            if (annotationNamesProvider.isShallowReferenceAlias(ann)) {
                hasShallowReference = true;
            }

            if (annotationNamesProvider.isIgnoredTypeAlias(ann)) {
                hasIgnored = true;
            }

            if (annotationNamesProvider.isValueObjectAlias(ann)) {
                hasValueObject = true;
            }

            if (annotationNamesProvider.isIgnoreDeclaredPropertiesAlias(ann)){
                hasIgnoreDeclaredProperties = true;
            }
        }

        return new ClassAnnotationsScan(hasValue,
                                        hasValueObject,
                                        hasEntity,
                                        hasShallowReference,
                                        hasIgnored,
                                        hasIgnoreDeclaredProperties,
                                        typeName);
    }
}
