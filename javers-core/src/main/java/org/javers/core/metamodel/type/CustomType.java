package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.custom.CustomPropertyComparator;
import java.lang.reflect.Type;

/**
 * Custom Type in client's domain model.
 * <br/><br/>
 *
 * JaVers treats a Custom Type as a black box
 * and doesn't take any assumptions about its content or behaviour.
 * It's a "not modeled" type, somehow similar to unbounded wildcard {@code <?>}.
 * <br/><br/>
 *
 * Objects of Custom Type are compared by a {@link CustomPropertyComparator},
 * and registering this comparator (see {@link JaversBuilder#registerCustomComparator(CustomPropertyComparator, Class)}
 * is the only way to map a Custom Type.
 * <br/><br/>
 *
 * Custom Types are serialized to JSON using Gson defaults.
 *
 * @author bartosz walacik
 */
public class CustomType<T> extends ClassType {
    private CustomPropertyComparator<T, ?> comparator;

    public CustomType(Type baseJavaType, CustomPropertyComparator<T, ?> comparator) {
        super(baseJavaType);
        Validate.argumentIsNotNull(comparator);
        this.comparator = comparator;
    }

    @Override
    public boolean equals(Object left, Object right) {
        return comparator.equals((T)left, (T)right);
    }

    public CustomPropertyComparator<T, ?> getComparator() {
        return comparator;
    }
}