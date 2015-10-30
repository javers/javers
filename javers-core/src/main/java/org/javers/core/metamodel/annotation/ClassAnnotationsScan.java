package org.javers.core.metamodel.annotation;

import org.javers.common.collections.Optional;

/**
 * @author bartosz.walacik
 */
public class ClassAnnotationsScan {
    private final boolean hasValue;
    private final boolean hasValueObject;
    private final boolean hasEntity;
    private final Optional<String> typeName;

    ClassAnnotationsScan(boolean hasValue, boolean hasValueObject, boolean hasEntity, Optional<String> typeName) {
        this.hasValue = hasValue;
        this.hasValueObject = hasValueObject;
        this.hasEntity = hasEntity;
        this.typeName = typeName;
    }

    public boolean hasValue() {
        return hasValue;
    }

    public boolean hasValueObject() {
        return hasValueObject;
    }

    public boolean hasEntity() {
        return hasEntity;
    }

    public Optional<String> typeName() {
        return typeName;
    }
}
