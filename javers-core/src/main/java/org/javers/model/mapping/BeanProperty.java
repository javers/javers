package org.javers.model.mapping;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.common.validation.Validate;
import org.javers.common.validation.Validate;
import org.javers.model.mapping.type.JaversType;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.persistence.Id;
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

    public BeanProperty(Method getter, JaversType javersType) {

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
    public Object get(Object target) {
        return ReflectionUtil.invokeGetterEvenIfPrivate(getter,target);
    }

    @Override
    public boolean isNull(Object target) {
        return get(target) == null;
    }

    @Override
    public boolean isId() {
        return getter.isAnnotationPresent(Id.class);
    }

    @Override
    public JaversType getType() {
        return javersType;
    }

    @Override
    public void setValue(Object value) {
        throw new NotImplementedException();
    }
}
