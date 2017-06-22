package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;
import org.javers.core.diff.custom.CustomValueComparator;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public abstract class PrimitiveOrValueType extends JaversType{
    private final CustomValueComparator valueComparator;

    PrimitiveOrValueType(Type baseJavaType) {
        super(baseJavaType);
        this.valueComparator = super::equals;
    }

    PrimitiveOrValueType(Type baseJavaType, CustomValueComparator customValueComparator) {
        super(baseJavaType);
        Validate.argumentIsNotNull(customValueComparator);
        this.valueComparator = customValueComparator;
    }

    @Override
    public boolean equals(Object left, Object right) {
        return valueComparator.equals(left, right);
    }
}
