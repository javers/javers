package org.javers.core.metamodel.property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class FieldBasedPropertyScanner implements PropertyScanner {
    private static final Logger logger = LoggerFactory.getLogger(FieldBasedPropertyScanner.class);

    @Override
    public  List<Property> scan(Class<?> managedClass) {
        List<Field> declaredFields = new LinkedList<>();
        declaredFields.addAll(getFields(managedClass));
        List<Property> propertyList = new ArrayList<>(declaredFields.size());

        for (Field field : declaredFields) {
            if(isPersistent(field)) {
                Property fieldProperty = new FieldProperty(field);
                propertyList.add(fieldProperty);
            }
        }
        return propertyList;
    }

    private List<Field> getFields(Class<?> clazz) {
        List<Field> superFields;
        if (clazz.getSuperclass() == Object.class) { //recursion stop condition
            superFields = new ArrayList<>();
        }
        else {
            superFields = getFields(clazz.getSuperclass());
        }

        superFields.addAll( Arrays.asList(clazz.getDeclaredFields()) );
        return superFields;
    }

    private boolean isPersistent(Field field) {
        return Modifier.isTransient(field.getModifiers()) == false
            && field.getAnnotation(Transient.class) == null
            && !field.getName().equals("this$0"); //owner of inner class
    }
}
