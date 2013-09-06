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
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class FieldBasedEntityFactory extends EntityFactory {

    public FieldBasedEntityFactory(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public <S> Entity<S> createEntity(Class<S> entityClass) {
        typeMapper.registerReferenceType(entityClass);
        List<Property> beanProperties = getManagedClassProperties(entityClass);
        return new Entity<S>(entityClass,beanProperties);
    }

    private <S> List<Property> getManagedClassProperties(Class<S> valueObjectClass) {
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

    private <S> void objectFields(Class<S> beanClass, List<Field> fields) {

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
