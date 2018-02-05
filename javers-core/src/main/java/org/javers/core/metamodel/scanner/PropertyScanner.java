package org.javers.core.metamodel.scanner;

import org.javers.common.reflection.JaversMember;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Managed class property propertyScanner
 *
 * @author pawel szymczyk
 */
abstract class PropertyScanner {

    private final AnnotationNamesProvider annotationNamesProvider;

    PropertyScanner(AnnotationNamesProvider annotationNamesProvider) {
        this.annotationNamesProvider = annotationNamesProvider;
    }

    public PropertyScan scan(Class<?> managedClass, boolean ignoreDeclaredProperties) {
        List<Property> properties = new ArrayList<>();
        for (JaversMember member : getMembers(managedClass)) {
            boolean isIgnoredInType = ignoreDeclaredProperties && member.getDeclaringClass().equals(managedClass);
            boolean hasTransientAnn = annotationNamesProvider.hasTransientPropertyAnn(member.getAnnotationTypes());
            boolean hasShallowReferenceAnn = annotationNamesProvider.hasShallowReferenceAnn(member.getAnnotationTypes());
            boolean hasIncludeAnn = annotationNamesProvider.hasDiffIncludeAnn(member.getAnnotationTypes());

            Optional<String> customPropertyName = annotationNamesProvider.findPropertyNameAnnValue(member.getAnnotations());
            properties.add(new Property(member, hasTransientAnn || isIgnoredInType, hasShallowReferenceAnn, customPropertyName, hasIncludeAnn));
        }
        return new PropertyScan(properties);
    }

    abstract List<JaversMember> getMembers(Class<?> managedClass);

    public PropertyScan scan(Class<?> managedClass) {
        return scan(managedClass, false);
    }
}
