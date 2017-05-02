package org.javers.core.metamodel.scanner;

import org.javers.core.metamodel.type.*;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @author bartosz.walacik
 */
class TypeFromAnnotation {
    private final Optional<Class<? extends JaversType>> javersType;

    TypeFromAnnotation(Class<? extends Annotation> javersTypeAnnotation) {
        if (javersTypeAnnotation == JaversAnnotationsNameSpace.VALUE_ANN) {
            javersType = Optional.of(ValueType.class);
        } else
        if (javersTypeAnnotation == JaversAnnotationsNameSpace.VALUE_OBJECT_ANN) {
            javersType = Optional.of(ValueObjectType.class);
        } else
        if (javersTypeAnnotation == JaversAnnotationsNameSpace.ENTITY_ANN) {
            javersType = Optional.of(EntityType.class);
        } else
        if (javersTypeAnnotation == JaversAnnotationsNameSpace.DIFF_IGNORE_ANN) {
            javersType = Optional.of(IgnoredType.class);
        } else
        if (javersTypeAnnotation == JaversAnnotationsNameSpace.SHALLOW_REFERENCE_ANN) {
            javersType = Optional.of(ShallowReferenceType.class);
        } else {
            javersType = Optional.empty();
        }
    }

    TypeFromAnnotation(boolean hasEntity, boolean hasValueObject, boolean hasValue) {
        if (hasEntity){
            javersType = Optional.of(EntityType.class);
        } else
        if (hasValueObject) {
            javersType = Optional.of(ValueObjectType.class);
        } else
        if (hasValue) {
            javersType = Optional.of(ValueType.class);
        } else {
            javersType = Optional.empty();
        }
    }

    boolean isValue() {
        return javersType.map(it -> it == ValueType.class).orElse(false);
    }

    boolean isValueObject() {
        return javersType.map(it -> it == ValueObjectType.class).orElse(false);
    }

    boolean isEntity() {
        return javersType.map(it -> it == EntityType.class).orElse(false);
    }

    boolean isIgnored() {
        return javersType.map(it -> it == IgnoredType.class).orElse(false);
    }

    boolean isShallowReference() {
        return javersType.map(it -> it == ShallowReferenceType.class).orElse(false);
    }
}
