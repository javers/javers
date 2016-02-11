package org.javers.core.metamodel.scanner;

import org.javers.common.collections.Optional;
import org.javers.core.metamodel.property.Property;

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

    public List<Property> getLooksLikeId() {
        return propertyScan.getLooksLikeId();
    }

    public boolean hasIdProperty() {
        return propertyScan.hasId();
    }

    public boolean hasValueAnn() {
        return classAnnotationsScan.hasValue();
    }

    public boolean hasValueObjectAnn() {
        return classAnnotationsScan.hasValueObject();
    }

    public boolean hasEntityAnn() {
        return classAnnotationsScan.hasEntity();
    }

    public boolean hasShallowReferenceAnn() {
        return classAnnotationsScan.hasShallowReference();
    }

    public Optional<String> typeName() {
        return classAnnotationsScan.typeName();
    }

    public boolean hasIgnoredAnn() {
        return classAnnotationsScan.hasIgnored();
    }
}
