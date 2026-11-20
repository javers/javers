package org.javers.core.metamodel.scanner;

import org.javers.common.collections.Sets;
import org.javers.common.reflection.JaversMember;
import org.javers.core.metamodel.annotation.Cascading;
import org.javers.core.metamodel.annotation.DiffIgnoreProperties;
import org.javers.core.metamodel.property.Property;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            boolean isIgnoredInType = isIgnoredInIgnoreDeclaredProperties(managedClass, member, ignoreDeclaredProperties) ||
                    isIgnoredInDiffIgnoreProperties(managedClass, member);
            boolean hasTransientAnn = annotationNamesProvider.hasTransientPropertyAnn(member.getAnnotationTypes());
            boolean hasShallowReferenceAnn = annotationNamesProvider.hasShallowReferenceAnn(member.getAnnotationTypes());
            boolean hasIncludeAnn = annotationNamesProvider.hasDiffIncludeAnn(member.getAnnotationTypes());
            Optional<Boolean> manualCascadeConfiguration = getManualCascadeConfiguration(member.getAnnotations());

            Optional<String> customPropertyName = annotationNamesProvider.findPropertyNameAnnValue(member.getAnnotations());
            properties.add(new Property(member, hasTransientAnn || isIgnoredInType, hasShallowReferenceAnn, customPropertyName, hasIncludeAnn, manualCascadeConfiguration.orElse(null)));
        }
        return new PropertyScan(properties);
    }

    private Optional<Boolean> getManualCascadeConfiguration(final Set<Annotation> annTypes) {
        return annTypes.stream().filter(annType -> annType instanceof Cascading)
            .findFirst()
            .map(annType -> ((Cascading) annType).value());
    }

    abstract List<JaversMember> getMembers(Class<?> managedClass);

    public PropertyScan scan(Class<?> managedClass) {
        return scan(managedClass, false);
    }


    private boolean isIgnoredInIgnoreDeclaredProperties(Class<?> managedClass, JaversMember member, boolean ignoreDeclaredProperties) {
        return ignoreDeclaredProperties && member.getDeclaringClass().equals(managedClass);
    }

    /**
     * Checks if a given member is marked to be ignored by the {@link DiffIgnoreProperties}
     */
    private boolean isIgnoredInDiffIgnoreProperties(Class<?> managedClass, JaversMember member) {
        return annotationNamesProvider.hasIgnorePropertiesAnn(managedClass)
                && annotationNamesProvider.getIgnoreFieldsAnnValue(Sets.asSet(managedClass.getAnnotations())).contains(member.name());
    }
}
