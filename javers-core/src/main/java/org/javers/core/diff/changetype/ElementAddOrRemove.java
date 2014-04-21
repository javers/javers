package org.javers.core.diff.changetype;

/**
 * Change on collection of values
 *
 * @author bartosz walacik
 */
abstract class ElementAddOrRemove extends ContainerElementChange {
    final Value value;

    ElementAddOrRemove(Object value) {
        this.value = new Value(value);
    }

    ElementAddOrRemove(Integer index, Object value) {
        super(index);
        this.value = new Value(value);
    }
}
