package org.javers.core.diff.changetype;

import org.javers.core.diff.NodePair;
import org.javers.core.metamodel.type.JaversProperty;

public class ValueChangeFactory {
    public static ValueChange create(NodePair pair, JaversProperty property, Object left, Object right) {
        if (pair.getLeft().isEdge()) {
            return new InitialValueChange(pair.createPropertyChangeMetadata(property), right);
        }
        if (pair.getRight().isEdge()) {
            return new TerminalValueChange(pair.createPropertyChangeMetadata(property), left);
        }
        return new ValueChange(pair.createPropertyChangeMetadata(property), left, right);
    }
}
