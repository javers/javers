package org.javers.core.metamodel.property;

import org.javers.common.reflection.JaversField;
import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.clazz.AnnotationNamesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class FieldBasedPropertyScanner implements PropertyScanner {

    private final AnnotationNamesProvider annotationNamesProvider;

    public FieldBasedPropertyScanner(AnnotationNamesProvider annotationNamesProvider) {
        this.annotationNamesProvider = annotationNamesProvider;
    }

    @Override
    public  List<Property> scan(Class<?> managedClass) {
        List<JaversField> fields = ReflectionUtil.getAllPersistentFields(managedClass);
        List<Property> propertyList = new ArrayList<>(fields.size());

        for (JaversField field : fields) {

            if (field.hasAnyAnnotation(annotationNamesProvider.getTransientAliases())){
                continue;
            }

            propertyList.add(new Property(field));
        }
        return propertyList;
    }
}
