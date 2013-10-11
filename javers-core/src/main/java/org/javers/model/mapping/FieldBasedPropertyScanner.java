package org.javers.model.mapping;

import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author pawel szymczyk
 */
public class FieldBasedPropertyScanner extends PropertyScanner {

    private static volatile FieldBasedPropertyScanner scanner;

    private FieldBasedPropertyScanner(TypeMapper typeMapper) {
        super(typeMapper);
    }

    public static FieldBasedPropertyScanner getInstane(TypeMapper typeMapper) {
        return  getSingletonInstance(typeMapper);
    }

    private static FieldBasedPropertyScanner getSingletonInstance(TypeMapper typeMapper) {
        if (scanner == null) {
            synchronized (PropertyScanner.class) {
                if (scanner == null) {
                    scanner = new FieldBasedPropertyScanner(typeMapper);
                }
            }
        }
        return scanner;
    }

    @Override
    public  List<Property> scan(Class<?> valueObjectClass) {
        List<Field> declaredFields = new LinkedList<Field>();
        objectFields(valueObjectClass, declaredFields);
        List<Property> propertyList = new ArrayList<Property>(declaredFields.size());

        for (Field field : declaredFields) {

            if(fieldIsPersistance(field)) {

                JaversType javersType = typeMapper.getJavesrType(field.getType());
                Property fieldProperty = new FieldProperty(field, javersType);
                propertyList.add(fieldProperty);
            }
        }
        return propertyList;
    }

    private void objectFields(Class<?> beanClass, List<Field> fields) {

        if(beanClass.getSuperclass() != null && !beanClass.getSuperclass().isInstance(Object.class)) {
            objectFields(beanClass.getSuperclass(), fields);
        }

        fields.addAll(Arrays.asList(beanClass.getDeclaredFields()));
    }

    private boolean fieldIsPersistance(Field field) {
        return Modifier.isTransient(field.getModifiers()) == false
                && field.getAnnotation(Transient.class) == null;
    }
}
