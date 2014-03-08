package org.javers.core.diff.changetype;

/**
 * Change on collection of values
 *
 * @author bartosz walacik
 */
abstract class ElementAddOrRemove extends ContainerValueChange {
    final Value value;

    ElementAddOrRemove(Object value) {
        this.value = new Value(value);
    }
}
