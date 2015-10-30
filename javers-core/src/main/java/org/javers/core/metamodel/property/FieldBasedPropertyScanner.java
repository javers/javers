package org.javers.core.metamodel.property;

import org.javers.common.reflection.JaversField;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.annotation.AnnotationNamesProvider;

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
    public List<Property> scan(Class<?> managedClass) {
        List<JaversField> fields = ReflectionUtil.getAllPersistentFields(managedClass);
        List<Property> propertyList = new ArrayList<>(fields.size());

        for (JaversField field : fields) {

            boolean hasTransientAnn = field.hasAnyAnnotation(annotationNamesProvider.getTransientAliases());

            propertyList.add(new Property(field, hasTransientAnn));
        }
        return propertyList;
    }

    @Override
    public Property scanSingleProperty(Class<?> managedClass, String propertyName) {
        JaversField field = ReflectionUtil.getPersistentField(managedClass, propertyName);
        boolean hasTransientAnn = field.hasAnyAnnotation(annotationNamesProvider.getTransientAliases());

        return new Property(field, hasTransientAnn);
    }
}
