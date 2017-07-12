package org.javers.core.metamodel.scanner;

import java.util.Optional;

/**
 * @author bartosz.walacik
 */
class ClassAnnotationsScan {
    private final TypeFromAnnotation typeFromAnnotation;
    private final boolean hasIgnoreDeclaredProperties;
    private final Optional<String> typeName;

    ClassAnnotationsScan(TypeFromAnnotation typeFromAnnotation,
                         boolean hasIgnoreDeclaredProperties,
                         Optional<String> typeName) {
        this.typeFromAnnotation = typeFromAnnotation;
        this.typeName = typeName;
        this.hasIgnoreDeclaredProperties = hasIgnoreDeclaredProperties;
    }

    public boolean isValue() {
        return typeFromAnnotation.isValue();
    }

    public boolean isValueObject() {
        return typeFromAnnotation.isValueObject();
    }

    public boolean isEntity() {
        return typeFromAnnotation.isEntity();
    }

    public boolean isShallowReference() {
        return typeFromAnnotation.isShallowReference();
    }

    public boolean isIgnored() {
        return typeFromAnnotation.isIgnored();
    }

    public Optional<String> typeName() {
        return typeName;
    }

    public boolean hasIgnoreDeclaredProperties() {
        return hasIgnoreDeclaredProperties;
    }
}
