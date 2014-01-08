package org.javers.model.mapping;

import org.javers.core.exceptions.JaversException;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarException;

/**
 * @author pawel szymczyk
 */
public class FieldBasedPropertyScanner extends PropertyScanner {
    private static final Logger logger = LoggerFactory.getLogger(FieldBasedPropertyScanner.class);

    public FieldBasedPropertyScanner(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public  List<Property> scan(Class<?> managedClass) {
        List<Field> declaredFields = new LinkedList<>();
        declaredFields.addAll(getFields(managedClass));
        List<Property> propertyList = new ArrayList<>(declaredFields.size());

        for (Field field : declaredFields) {
            if(isPersistent(field)) {
                JaversType javersType = getJaversType(field);

                Property fieldProperty = new FieldProperty(field, javersType);
                propertyList.add(fieldProperty);
            }
        }
        return propertyList;
    }

    private JaversType getJaversType(Field field) {
        JaversType javersType = null;
        try {
            return typeMapper.getJaversType(field.getGenericType());
        } catch (JaversException e) {
            logger.error("caught {} when scanning field {}",e,field);
            throw e;
        }
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
