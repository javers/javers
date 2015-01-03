package org.javers.core.metamodel.property;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.core.metamodel.clazz.AnnotationNamesProvider;
import org.javers.core.metamodel.clazz.ClassAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
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
        List<Field> fields = ReflectionUtil.getAllPersistentFields(managedClass);
        List<Property> propertyList = new ArrayList<>(fields.size());

        for (Field field : fields) {

            if (ReflectionUtil.hasAnyAnnotation(field, annotationNamesProvider.getTransientAliases())){
                continue;
            }

            Property fieldProperty = new FieldProperty(field);
            propertyList.add(fieldProperty);
        }
        return propertyList;
    }
}
