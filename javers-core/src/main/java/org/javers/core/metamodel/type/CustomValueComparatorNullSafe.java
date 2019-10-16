package org.javers.core.metamodel.type;

import org.javers.common.validation.Validate;
import org.javers.core.diff.custom.CustomValueComparator;

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
