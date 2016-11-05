package org.javers.core.metamodel.scanner;

import org.javers.common.reflection.JaversField;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.annotation.IgnoreDeclaredProperties;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
class FieldBasedPropertyScanner implements PropertyScanner {

    private final AnnotationNamesProvider annotationNamesProvider;

    public FieldBasedPropertyScanner(AnnotationNamesProvider annotationNamesProvider) {
        this.annotationNamesProvider = annotationNamesProvider;
    }

    @Override
    public PropertyScan scan(Class<?> managedClass) {
        final IgnoreDeclaredProperties ignoreDeclaredPropertiesAnnotation = managedClass.getAnnotation(IgnoreDeclaredProperties.class);
        List<JaversField> fields = ReflectionUtil.getAllPersistentFields(managedClass);
        List<Property> propertyList = new ArrayList<>(fields.size());

        if (ignoreDeclaredPropertiesAnnotation != null) {
            for (JaversField field : fields) {
                propertyList.add(new Property(field, true));
            }
        } else {
            for (JaversField field : fields) {
                boolean hasTransientAnn = field.hasAnyAnnotation(annotationNamesProvider.getTransientAliases());
                boolean hasShallowReferenceAnn = field.hasAnyAnnotation(annotationNamesProvider.getShallowReferenceAliases());
                propertyList.add(new Property(field, hasTransientAnn, hasShallowReferenceAnn));
            }
        }
        return new PropertyScan(propertyList);
    }

}
