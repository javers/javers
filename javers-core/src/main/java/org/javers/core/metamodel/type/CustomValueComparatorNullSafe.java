package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;
import org.javers.core.diff.custom.CustomValueComparator;
import org.javers.core.metamodel.property.MissingProperty;

class CustomValueComparatorNullSafe<T> implements CustomValueComparator<T> {
    private final CustomValueComparator<T> delegate;

    CustomValueComparatorNullSafe(CustomValueComparator<T> delegate) {
        Validate.argumentIsNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public boolean equals(T o1, T o2) {
        if (o1 == null && o2 == null) {
            return true;
        }

        if ((o1 == null && o2 != null) || (o1 != null && o2 == null)) {
            return false;
        }

        if (o1 == MissingProperty.INSTANCE || o2 == MissingProperty.INSTANCE) {
            return false;
        }

        return delegate.equals(o1, o2);
    }

    @Override
    public String toString(T value) {
        if (value == null) {
            return "";
        }
        return delegate.toString(value);
    }
}
