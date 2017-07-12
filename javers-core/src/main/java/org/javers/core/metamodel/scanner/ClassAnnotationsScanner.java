package org.javers.core.metamodel.scanner;

import org.javers.common.collections.Lists;
import org.javers.common.collections.Sets;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.annotation.*;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.javers.core.metamodel.scanner.JaversAnnotationsNameSpace.IGNORE_DECLARED_PROPERTIES_ANN;

/**
 * Should scan well known annotations at class level
 *
 * @author bartosz walacik
 */
class ClassAnnotationsScanner {
    private final AnnotationNamesProvider annotationNamesProvider;
    private List<Class<? extends Annotation>> JAVERS_TYPE_ANNOTATIONS = Lists.immutableListOf(
            DiffIgnore.class,
            Entity.class,
            ShallowReference.class,
            ValueObject.class,
            Value.class
    );

    ClassAnnotationsScanner(AnnotationNamesProvider annotationNamesProvider) {
        this.annotationNamesProvider = annotationNamesProvider;
    }

    public ClassAnnotationsScan scan(Class javaClass){
        Validate.argumentIsNotNull(javaClass);

        Set<Annotation> annotations = Sets.asSet(javaClass.getAnnotations());
        Set<Class<? extends Annotation>> annTypes = annotations.stream()
                .map(a -> a.annotationType())
                .collect(Collectors.toSet());

        Optional<String> typeName = annotationNamesProvider.findTypeNameAnnValue(annotations);

        Optional<Class<? extends Annotation>> javersTypeAnnotation =
                JAVERS_TYPE_ANNOTATIONS.stream().filter(annTypes::contains).findFirst();

        boolean hasIgnoreDeclaredProperties = annTypes.contains(IGNORE_DECLARED_PROPERTIES_ANN);

        return new ClassAnnotationsScan(typeFromAnnotation(annTypes),
                                        hasIgnoreDeclaredProperties,
                                        typeName);
    }

    private TypeFromAnnotation typeFromAnnotation(Set<Class<? extends Annotation>> annTypes) {
        Optional<Class<? extends Annotation>> javersTypeAnnotation =
                JAVERS_TYPE_ANNOTATIONS.stream().filter(annTypes::contains).findFirst();

        if (javersTypeAnnotation.isPresent()) {
            return new TypeFromAnnotation(javersTypeAnnotation.get());
        }
        else {
            boolean hasValue = annotationNamesProvider.hasValueAnnAlias(annTypes);
            boolean hasValueObject = annotationNamesProvider.hasValueObjectAnnAlias(annTypes);
            boolean hasEntity = annotationNamesProvider.hasEntityAnnAlias(annTypes);
            return new TypeFromAnnotation(hasEntity, hasValueObject, hasValue);
        }
    }
}
