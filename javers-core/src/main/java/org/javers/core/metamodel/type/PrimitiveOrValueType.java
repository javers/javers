package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;
import org.javers.core.diff.custom.CustomValueComparator;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * @author bartosz walacik
 */
public abstract class PrimitiveOrValueType extends JaversType{
    private final Optional<CustomValueComparator> customValueComparator;

    PrimitiveOrValueType(Type baseJavaType) {
        this(baseJavaType, Optional.empty());
    }

    PrimitiveOrValueType(Type baseJavaType, Optional<CustomValueComparator> customValueComparator) {
        super(baseJavaType);
        Validate.argumentIsNotNull(customValueComparator);
        this.customValueComparator = customValueComparator;
    }

    @Override
    public boolean equals(Object left, Object right) {
        return customValueComparator.map(it -> it.equals(left, right)).orElse(super.equals(left, right));
    }
}
