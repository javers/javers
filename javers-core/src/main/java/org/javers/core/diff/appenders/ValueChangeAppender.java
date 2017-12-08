package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.type.*;

import java.util.Objects;

/**
 * @author bartosz walacik
 */
class ValueChangeAppender extends CorePropertyChangeAppender<ValueChange> {

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

        //special treatment for EmbeddedId - could be ValueObjects without good equals()
        /*if (isIdProperty(pair, property)) {
            PrimitiveOrValueType idPropertyType = property.getType();

            if (Objects.equals(idPropertyType.smartToString(leftValue),
                               idPropertyType.smartToString(rightValue))) {
                return null;
            }
        } else {
            if (property.getType().equals(leftValue, rightValue)) {
                return null;
            }
        }*/

        if (property.getType().equals(leftValue, rightValue)) {
            return null;
        }

        return new ValueChange(pair.getGlobalId(), property.getName(), leftValue, rightValue);
    }

    private boolean isIdProperty(NodePair nodePair, JaversProperty property){
        ManagedType managedType = nodePair.getManagedType();

        if (managedType instanceof EntityType) {
            return ((EntityType)managedType).getIdProperty().equals(property);
        }
        return false;
    }
}
