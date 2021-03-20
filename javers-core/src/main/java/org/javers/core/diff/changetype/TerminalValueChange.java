package org.javers.core.diff.changetype;

import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.type.PrimitiveOrValueType;

/**
 * TerminalValueChange is a subtype of ValueChange with a property value on left and null on right.
 * It is generated for each Primitive or Value property of a Removed Object to capture its state.
 *
 * @see ObjectRemoved
 * @see PrimitiveOrValueType
 * @see JaversBuilder#withTerminalChanges(boolean)
 */
public class TerminalValueChange extends ValueChange {

    public TerminalValueChange(PropertyChangeMetadata metadata, Object leftValue) {
        super(metadata, leftValue, null);
    }
}
