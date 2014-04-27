package org.javers.core.diff.changetype.container;

import org.javers.core.diff.changetype.Atomic;

/**
 * Change on collection of values
 *
 * @author bartosz walacik
 */
abstract class ValueAddOrRemove extends ContainerElementChange {
    final Atomic value;

    ValueAddOrRemove(Object value) {
        this.value = new Atomic(value);
    }

    ValueAddOrRemove(Integer index, Object value) {
        super(index);
        this.value = new Atomic(value);
    }
}
