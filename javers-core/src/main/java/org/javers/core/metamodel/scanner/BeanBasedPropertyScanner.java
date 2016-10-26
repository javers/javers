package org.javers.core.metamodel.scanner;

import org.javers.common.reflection.JaversField;
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

        if (ignoreDeclaredPropertiesAnnotation != null) {
            List<JaversField> fields = ReflectionUtil.getAllPersistentFields(managedClass);
            List<Property> beanProperties = new ArrayList<>(fields.size());

            for (JaversField field : fields) {
                beanProperties.add(new Property(field, true));
            }
            return new PropertyScan(beanProperties);

        } else {
            List<Property> beanProperties = new ArrayList<>();
            List<JaversMethod> getters = ReflectionUtil.findAllPersistentGetters(managedClass);
            for (JaversMethod getter : getters) {

                boolean hasTransientAnn = getter.hasAnyAnnotation(annotationNamesProvider.getTransientAliases());
                beanProperties.add(new Property(getter, hasTransientAnn));
            }
            return new PropertyScan(beanProperties);
        }

    }
}
