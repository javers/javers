package org.javers.core.metamodel.scanner;

import org.javers.common.reflection.JaversMethod;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.annotation.IgnoreDeclaredProperties;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
class BeanBasedPropertyScanner implements PropertyScanner {

    private final AnnotationNamesProvider annotationNamesProvider;

    BeanBasedPropertyScanner(AnnotationNamesProvider annotationNamesProvider) {
        this.annotationNamesProvider = annotationNamesProvider;
    }

    @Override
    public PropertyScan scan(Class<?> managedClass) {
        final IgnoreDeclaredProperties ignoreDeclaredPropertiesAnnotation = managedClass.getAnnotation(IgnoreDeclaredProperties.class);
        List<JaversMethod> getters = ReflectionUtil.findAllPersistentGetters(managedClass);
        List<Property> beanProperties = new ArrayList<>();

        if (ignoreDeclaredPropertiesAnnotation != null) {
            for (JaversMethod getter : getters) {
                beanProperties.add(new Property(getter, true));
            }
        } else {
            for (JaversMethod getter : getters) {
                boolean hasTransientAnn = getter.hasAnyAnnotation(annotationNamesProvider.getTransientAliases());
                boolean hasShallowReferenceAnn = getter.hasAnyAnnotation(annotationNamesProvider.getShallowReferenceAliases());
                beanProperties.add(new Property(getter, hasTransientAnn, hasShallowReferenceAnn));
            }

        }
        return new PropertyScan(beanProperties);
    }

}
