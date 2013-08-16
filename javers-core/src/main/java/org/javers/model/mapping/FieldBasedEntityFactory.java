package org.javers.model.mapping;

import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class FieldBasedEntityFactory extends EntityFactory {

    public FieldBasedEntityFactory(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public <S> Entity<S> create(Class<S> entityClass) {
        typeMapper.registerReferenceType(entityClass);

        Field[] declaredFields = entityClass.getDeclaredFields();
        List<Property> propertyList = new ArrayList<>(declaredFields.length);

        for (Field field : declaredFields) {

            JaversType javersType = typeMapper.mapType(field.getType());
            Property fieldProperty = new FieldProperty(field, javersType);
            propertyList.add(fieldProperty);
        }

        return new Entity<S>(entityClass,propertyList);
    }
}
