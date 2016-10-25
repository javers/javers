package org.javers.core.diff.changetype.container;

import java.util.Objects;

import org.javers.core.diff.changetype.Atomic;

/**
 * Change on collection of values
 *
 * @author bartosz walacik
 */
public abstract class ValueAddOrRemove extends ContainerElementChange {
    final Atomic value;

    ValueAddOrRemove(Object value) {
        this.value = new Atomic(value);
    }

    ValueAddOrRemove(Integer index, Object value) {
        super(index);
        this.value = new Atomic(value);
    }

    public Object getValue() {
        return value.unwrap();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ValueAddOrRemove) {
            ValueAddOrRemove that = (ValueAddOrRemove) obj;
            return super.equals(that)
                    && Objects.equals(this.getValue(), that.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getValue());
    }

}
