package org.javers.core.diff.changetype.container;

import org.javers.core.diff.changetype.Atomic;

import java.util.Objects;

/**
 * internal wrapper for Collection or Array
 */
class Container<T> {
    private final T value;

    Container(T value) {
        this.value = value;
    }

    public T unwrap() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Container)) {
            return false;
        }

        Container other = (Container)obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        if (value == null) {
            return 0;
        }
        return value.hashCode();
    }
}
