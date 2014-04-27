package org.javers.core.diff.changetype.container;

import org.javers.core.diff.changetype.Value;
import org.javers.core.diff.changetype.container.ContainerElementChange;

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
