package org.javers.core.diff.changetype;

/**
 * TODO desc and print
 */
public class InitialValueChange extends ValueChange {
    public InitialValueChange(PropertyChangeMetadata metadata, Object rightValue) {
        super(metadata, null, rightValue);
    }
}
