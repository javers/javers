package org.javers.common.reflection;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bartosz walacik
 */
class JaversFieldFactory {

    private static final Logger logger = LoggerFactory.getLogger(JaversFieldFactory.class);

    private final Class methodSource;

    public JaversFieldFactory(Class methodSource) {
        this.methodSource = methodSource;
    }

    public List<JaversField> getAllFields(){
        List<JaversField> fields = new ArrayList<>();
        TypeResolvingContext context = new TypeResolvingContext();

        Class clazz = methodSource;
        while (clazz != null && clazz != Object.class)  {
            context.addTypeSubstitutions(clazz);

            for (Field f : clazz.getDeclaredFields()){
                fields.add(createJField(f,context));
            }

            clazz = clazz.getSuperclass();
        }

        return fields;
    }

    public JaversField getField(String fieldName) {
        return createJField(getDeclaredField(fieldName), new TypeResolvingContext());
    }

    private Field getDeclaredField(String fieldName) {
        try {
            return methodSource.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            logger.debug("Class {} doesn't have a field {}", methodSource.getName(), fieldName);
            throw new JaversException(JaversExceptionCode.PROPERTY_NOT_FOUND, fieldName, methodSource.getName());
        }
    }

    private JaversField createJField(Field rawField, TypeResolvingContext context) {
        Type actualType = context.getSubstitution(rawField.getGenericType());
        return new JaversField(rawField, actualType);
    }
}
