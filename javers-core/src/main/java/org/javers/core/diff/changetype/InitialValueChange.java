package org.javers.core.diff.changetype;

/**
 * TODO desc and print
 * (!) fake ObjectNode !
 */
public class InitialValueChange extends ValueChange {

    public InitialValueChange(PropertyChangeMetadata metadata, Object rightValue) {
        super(metadata, null, rightValue);
    }
}
