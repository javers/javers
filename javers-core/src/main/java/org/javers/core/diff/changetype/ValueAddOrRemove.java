package org.javers.core.diff.changetype;

/**
 * Change on collection of values
 *
 * @author bartosz walacik
 */
abstract class ValueAddOrRemove extends ContainerElementChange {
    final Value value;

    ValueAddOrRemove(Object value) {
        this.value = new Value(value);
    }

    ValueAddOrRemove(Integer index, Object value) {
        super(index);
        this.value = new Value(value);
    }
}
