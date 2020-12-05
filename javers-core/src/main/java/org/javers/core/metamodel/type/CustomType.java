package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.PropertyChangeMetadata;
import org.javers.core.diff.custom.CustomPropertyComparator;
import org.javers.core.metamodel.property.Property;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * <b>
 * Custom Types are not easy to manage, use it as a last resort,<br/>
 * only for corner cases like comparing custom Collection types.</b>
 * <br/><br/>
 *
 * JaVers treats a Custom Type as a black box
 * and doesn't take any assumptions about its content or behaviour.
 * It's a "not modeled" type, somehow similar to unbounded wildcard {@code <?>}.
 * <br/><br/>
 *
 * Objects of Custom Type are compared by a {@link CustomPropertyComparator}.
 * Registering this comparator is the only way to map a Custom Type.
 * <br/><br/>
 *
 * Custom Types are serialized to JSON using Gson defaults.
 *
 * @param <T> Custom Type
 * @see JaversBuilder#registerCustomType(Class, CustomPropertyComparator)
 */
public class CustomType<T> extends ClassType implements CustomComparableType {
    private final CustomPropertyComparatorNullSafe<T, ?> comparator;

    public CustomType(Type baseJavaType, CustomPropertyComparator<T, ?> comparator) {
        super(baseJavaType);
        Validate.argumentIsNotNull(comparator);
        this.comparator = new CustomPropertyComparatorNullSafe(comparator);
    }

    @Override
    public boolean equals(Object left, Object right) {
        return comparator.equals((T)left, (T)right);
    }

    CustomPropertyComparator<T, ?> getComparator() {
        return comparator;
    }

    @Override
    public boolean hasCustomValueComparator() {
        return true;
    }

    @Override
    public String valueToString(Object value) {
        return comparator.toString((T) value);
    }

    private static class CustomPropertyComparatorNullSafe<T, C extends PropertyChange>
            extends CustomValueComparatorNullSafe<T>
            implements CustomPropertyComparator<T, C>
    {
        private final CustomPropertyComparator<T, C> delegate;

        public CustomPropertyComparatorNullSafe(CustomPropertyComparator<T, C> delegate) {
            super(delegate);
            this.delegate = delegate;
        }

        @Override
        public Optional<C> compare(T left, T right, PropertyChangeMetadata metadata, Property property) {
            return delegate.compare(left, right, metadata, property);
        }
    }
}