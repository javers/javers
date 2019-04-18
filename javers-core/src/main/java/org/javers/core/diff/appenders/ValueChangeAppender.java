package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ValueAddedChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.ValueRemovedChange;
import org.javers.core.diff.changetype.ValueUpdatedChange;
import org.javers.core.metamodel.property.MissingProperty;
import org.javers.core.metamodel.type.*;

/**
 * @author bartosz walacik
 */
class ValueChangeAppender implements PropertyChangeAppender<ValueChange> {

    @Override
    public boolean supports(JaversType propertyType) {
        return  propertyType instanceof PrimitiveOrValueType;
    }

    /**
     * @param property supported property (of PrimitiveType or ValueObjectType)
     */
    @Override
    public ValueChange calculateChanges(NodePair pair, JaversProperty property) {

        Object leftValue = pair.getLeftPropertyValue(property);
        Object rightValue = pair.getRightPropertyValue(property);

        if(leftValue instanceof MissingProperty) {
            return new ValueAddedChange(pair.getGlobalId(), property.getName(), rightValue);
        } else if(rightValue instanceof MissingProperty) {
            return new ValueRemovedChange(pair.getGlobalId(), property.getName(), leftValue);
        }

        //special treatment for EmbeddedId - could be ValueObjects without good equals() implementation
        if (isIdProperty(pair, property)) {
            //For idProperty, only initial change is possible (from null to value).
            //If we have values on both sides, we know that they have the same String representation
            if (leftValue != null && rightValue != null) {
                return null;
            }
        } else {
            if (property.getType().equals(leftValue, rightValue)) {
                return null;
            }
        }

        return new ValueUpdatedChange(pair.getGlobalId(), property.getName(), leftValue, rightValue);
    }

    private boolean isIdProperty(NodePair nodePair, JaversProperty property){
        ManagedType managedType = nodePair.getManagedType();

        if (managedType instanceof EntityType) {
            return ((EntityType)managedType).isIdProperty(property);
        }
        return false;
    }
}
