package org.javers.core.diff.changetype;

/**
 * TODO desc and print
 */
public class TerminalValueChange extends ValueChange {

    public TerminalValueChange(PropertyChangeMetadata metadata, Object leftValue) {
        super(metadata, leftValue, null);
    }
}
