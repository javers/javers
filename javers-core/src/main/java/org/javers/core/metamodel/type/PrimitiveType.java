package org.javers.core.metamodel.type;

import org.javers.common.string.ToStringBuilder;

import java.lang.reflect.Type;

/**
 * Primitive or primitive box
 *
 * @author bartosz walacik
 */
public class PrimitiveType extends PrimitiveOrValueType {

    public PrimitiveType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public String valueToString(Object value) {
        return ToStringBuilder.smartToString(value);
    }
}
