package org.javers.core.metamodel.scanner;

import org.javers.common.reflection.JaversField;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.property.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
class FieldBasedPropertyScanner extends PropertyScanner {

    private final AnnotationNamesProvider annotationNamesProvider;

    public FieldBasedPropertyScanner(AnnotationNamesProvider annotationNamesProvider) {
        this.annotationNamesProvider = annotationNamesProvider;
    }

    @Override
    public PropertyScan scan(Class<?> managedClass, boolean ignoreDeclaredProperties) {
        List<JaversField> fields = ReflectionUtil.getAllPersistentFields(managedClass);
        List<Property> propertyList = new ArrayList<>(fields.size());

        for (JaversField field : fields) {
            boolean isIgnoredInType = ignoreDeclaredProperties && field.getDeclaringClass().equals(managedClass);
            boolean hasTransientAnn = field.hasAnyAnnotation(annotationNamesProvider.getTransientAliases());
            boolean hasShallowReferenceAnn = field.hasAnyAnnotation(annotationNamesProvider.getShallowReferenceAliases());
            if(field.hasAnyAnnotation(annotationNamesProvider.getPropertyNameAliases())){
                String customPropertyName = field.getRawMember().getAnnotation(PropertyName.class).value();
                propertyList.add(new Property(field, hasTransientAnn || isIgnoredInType, hasShallowReferenceAnn, customPropertyName));
            } else {
                propertyList.add(new Property(field, hasTransientAnn || isIgnoredInType, hasShallowReferenceAnn, field.propertyName()));
            }

        }
        return new PropertyScan(propertyList);
    }
}
