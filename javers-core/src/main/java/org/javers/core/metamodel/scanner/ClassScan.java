package org.javers.core.metamodel.scanner;

import org.javers.core.metamodel.property.Property;

import java.util.Optional;

import java.util.List;

/**
 * @author bartosz.walacik
 */
public class ClassScan {
    private final PropertyScan propertyScan;
    private final ClassAnnotationsScan classAnnotationsScan;

    public ClassScan(PropertyScan propertyScan, ClassAnnotationsScan classAnnotationsScan) {
        this.propertyScan = propertyScan;
        this.classAnnotationsScan = classAnnotationsScan;
    }

    public List<Property> getProperties() {
        return propertyScan.getProperties();
    }

    public boolean hasIdProperty() {
        return propertyScan.hasId();
    }

    public boolean hasValueAnn() {
        return classAnnotationsScan.isValue();
    }

    public boolean hasValueObjectAnn() {
        return classAnnotationsScan.isValueObject();
    }

    public boolean hasEntityAnn() {
        return classAnnotationsScan.isEntity();
    }

    public boolean hasShallowReferenceAnn() {
        return classAnnotationsScan.isShallowReference();
    }

    public Optional<String> typeName() {
        return classAnnotationsScan.typeName();
    }

    public boolean hasIgnoredAnn() {
        return classAnnotationsScan.isIgnored();
    }
}
