package org.javers.model.mapping;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.common.validation.Validate;
import org.javers.common.validation.Validate;
import org.javers.model.mapping.type.JaversType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.lang.reflect.Method;

import static org.javers.common.validation.Validate.*;

/**
 * Immutable
 *
 * @author bartosz walacik
 */
public class BeanProperty implements Property {
    private final Method getter;
    private final String name;
    private final JaversType javersType;

    protected BeanProperty(Method getter, JaversType javersType) {

        argumentIsNotNull(getter, "getter should not be null!");
        argumentIsNotNull(javersType, "javersType should not be null!");

        this.getter = getter;
        this.name = ReflectionUtil.getterToField(getter);
        this.javersType = javersType;
    }

    @Override
    public String getName() {
        return name;
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
