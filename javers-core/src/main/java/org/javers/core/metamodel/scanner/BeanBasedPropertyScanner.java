package org.javers.core.metamodel.scanner;

import org.javers.common.reflection.JaversMethod;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author pawel szymczyk
 */
class BeanBasedPropertyScanner extends PropertyScanner {

    private final AnnotationNamesProvider annotationNamesProvider;

    BeanBasedPropertyScanner(AnnotationNamesProvider annotationNamesProvider) {
        this.annotationNamesProvider = annotationNamesProvider;
    }

    @Override
    public PropertyScan scan(Class<?> managedClass, boolean ignoreDeclaredProperties) {
        List<JaversMethod> getters = ReflectionUtil.findAllPersistentGetters(managedClass);
        List<Property> beanProperties = new ArrayList<>();

        for (JaversMethod getter : getters) {
            boolean isIgnoredInType = ignoreDeclaredProperties && getter.getDeclaringClass().equals(managedClass);
            boolean hasTransientAnn = getter.hasAnyAnnotation(annotationNamesProvider.getTransientAliases());
            boolean hasShallowReferenceAnn = getter.hasAnyAnnotation(annotationNamesProvider.getShallowReferenceAliases());

            Optional<String> customPropertyName = getter.getFirstValue(annotationNamesProvider.getPropertyNameAliases());
            beanProperties.add(new Property(getter, hasTransientAnn || isIgnoredInType, hasShallowReferenceAnn, customPropertyName));

        }
        return new PropertyScan(beanProperties);
    }
}
