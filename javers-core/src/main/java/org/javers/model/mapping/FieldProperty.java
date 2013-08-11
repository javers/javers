package org.javers.model.mapping;

import org.javers.core.validation.Validate;
import org.javers.model.mapping.type.JaversType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Field;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class FieldProperty implements Property {

    private Field field;
    private JaversType javersType;

    public FieldProperty(Field field, JaversType javersType) {

        Validate.isNotNull(field, "Field should not be null!");
        Validate.isNotNull(javersType, "JaversType should not be null!");

        this.field = field;
        this.javersType = javersType;
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public JaversType getType() {
        return javersType;
    }

    @Override
    public Entity getRefEntity() {
        throw new NotImplementedException();
    }

    @Override
    public Object getValue() {
        throw new NotImplementedException();
    }

    @Override
    public void setValue(Object value) {
        throw new NotImplementedException();
    }
}
