package org.javers.model.mapping;

import org.javers.common.reflection.ReflectionUtil;
import org.javers.model.mapping.type.JaversType;

import java.lang.reflect.Method;

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
        if (getter == null) {
            throw new IllegalArgumentException("getter should not be null");
        }
        if (javersType == null) {
            throw new IllegalArgumentException("javersType should not be null");
        }

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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getValue() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setValue(Object value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
