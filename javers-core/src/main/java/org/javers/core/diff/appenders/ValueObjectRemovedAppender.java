package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.TerminalValueChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ValueObjectType;

/**
 * ValueObjects are typically represented as Nodes in the Graph.
 * - When ValueObject->property is changed, it will be recognized as ValueChange.
 * <br />
 * However, in specific cases (when object is restored from snapshot), Value object is referenced only by a property.
 * <br />
 * This appender is responsible for handling the edge case when ValueObject is set to NULL.
 *
 * @author michael olsavsky
 */
class ValueObjectRemovedAppender implements PropertyChangeAppender<ValueChange> {

    @Override
    public boolean supports(JaversType propertyType) {
        return propertyType instanceof ValueObjectType;
    }

    @Override
    public ValueChange calculateChanges(NodePair pair, JaversProperty property) {
        var leftValue = pair.getLeft().getGlobalId();
        var rightValue = pair.getRightPropertyValue(property);

        if (leftValue != null && rightValue == null) {
            return new TerminalValueChange(pair.createPropertyChangeMetadata(property), leftValue);
        }

        return null;
    }
}
