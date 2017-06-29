package org.javers.common.reflection;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import static org.javers.common.string.ToStringBuilder.typeName;

/**
 * @author bartosz walacik
 */
public class JaversField extends JaversMember<Field> {

    protected JaversField(Field rawField, Type resolvedReturnType) {
        super(rawField, resolvedReturnType);
    }

    @Override
    protected Type getRawGenericType() {
        return getRawMember().getGenericType();
    }

    @Override
    public Class<?> getRawType() {
        return getRawMember().getType();
    }

    @Override
    public Object getEvenIfPrivate(Object onObject) {
        try {
            return getRawMember().get(onObject);
        } catch (IllegalArgumentException ie){
            throw new JaversException(JaversExceptionCode.MISSING_PROPERTY, this, ie.getClass().getName());
        } catch (IllegalAccessException e) {
            throw new JaversException(JaversExceptionCode.PROPERTY_ACCESS_ERROR,
                  this, onObject.getClass().getSimpleName(), e.getClass().getName()+": "+e.getMessage());
        }
    }

    @Override
    public void setEvenIfPrivate(Object onObject, Object value) {
        try {
            getRawMember().set(onObject, value);
        } catch (IllegalArgumentException ie){
            String valueType = value == null ? "null" : value.getClass().getName();
            throw new JaversException(JaversExceptionCode.PROPERTY_SETTING_ERROR, valueType, this, ie.getClass().getName() + " - " + ie.getMessage());
        } catch (IllegalAccessException e) {
            throw new JaversException(JaversExceptionCode.PROPERTY_ACCESS_ERROR,
                    this, onObject.getClass().getSimpleName(), e.getClass().getName()+": "+e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Field " + typeName(getGenericResolvedType())+" " + name() +"; //declared in " +getDeclaringClass().getSimpleName();
    }
}
