package org.javers.core.metamodel.type;

import org.javers.common.collections.Primitives;
import org.javers.core.diff.custom.CustomValueComparator;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public abstract class PrimitiveOrValueType extends ClassType{
    private final CustomValueComparator valueComparator;

    PrimitiveOrValueType(Type baseJavaType) {
        this(baseJavaType, null);
    }

    PrimitiveOrValueType(Type baseJavaType, CustomValueComparator customValueComparator) {
        super(baseJavaType);
        this.valueComparator = customValueComparator == null ? super::equals : customValueComparator;
    }

    @Override
    public boolean equals(Object left, Object right) {
        return valueComparator.equals(left, right);
    }

    public String smartToString(Object cdo) {
        if (cdo == null){
            return "";
        }

        if (cdo instanceof String) {
            return (String)cdo;
        }

        return cdo.toString();
    }

    public boolean isNumber() {
        return Number.class.isAssignableFrom(getBaseJavaClass()) ||
               Primitives.isPrimitiveNumber(getBaseJavaClass());
    }

    public boolean isBoolean() {
        return Boolean.class == getBaseJavaClass() || boolean.class == getBaseJavaClass();
    }

    public boolean isStringy() {
        return String.class == getBaseJavaClass() ||
               CharSequence.class == getBaseJavaClass() ||
               char.class == getBaseJavaClass() ||
               Character.class == getBaseJavaClass();
    }
}
