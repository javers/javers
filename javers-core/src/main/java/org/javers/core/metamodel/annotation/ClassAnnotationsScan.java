package org.javers.core.metamodel.annotation;

import org.javers.common.collections.Optional;

/**
 * @author bartosz.walacik
 */
public class ClassAnnotationsScan {
    private final boolean hasValue;
    private final boolean hasValueObject;
    private final boolean hasEntity;
    private final boolean hasShallowReference;
    private final Optional<String> typeName;

    ClassAnnotationsScan(boolean hasValue, boolean hasValueObject, boolean hasEntity, boolean hasShallowReference, Optional<String> typeName) {
        this.hasValue = hasValue;
        this.hasValueObject = hasValueObject;
        this.hasEntity = hasEntity;
        this.hasShallowReference = hasShallowReference;
        this.typeName = typeName;
    }

    public boolean hasValue() {
        return hasValue;
    }

    public boolean hasValueObject() {
        return hasValueObject;
    }

    public boolean hasEntity() { return hasEntity; }

    public boolean isHasShallowReference() { return hasShallowReference; }

    public Optional<String> typeName() {
        return typeName;
    }
}
