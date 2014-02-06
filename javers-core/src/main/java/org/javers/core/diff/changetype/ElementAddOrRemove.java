package org.javers.core.diff.changetype;

/**
 * Change on collection of values
 *
 * @author bartosz walacik
 */
public abstract class ElementAddOrRemove extends ContainerValueChange {
    protected final Value value;

    protected ElementAddOrRemove(Object value) {
        this.value = new Value(value);
    }
}
