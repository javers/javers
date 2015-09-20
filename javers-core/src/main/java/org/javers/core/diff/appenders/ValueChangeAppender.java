package org.javers.core.diff.appenders;

import org.javers.common.collections.Objects;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.PrimitiveOrValueType;

import static org.javers.common.reflection.ReflectionUtil.reflectiveToString;

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
    public ValueChange calculateChanges(NodePair pair, Property property) {
        Object leftValue = pair.getLeftPropertyValue(property);
        Object rightValue = pair.getRightPropertyValue(property);

        //special treatment for EmbeddedId - could be ValueObjects without good equals() implementation
        if (isIdProperty(pair, property)) {
            if (Objects.nullSafeEquals(reflectiveToString(leftValue),
                                       reflectiveToString(rightValue))){
                return null;
            }
        }else {
            if (Objects.nullSafeEquals(leftValue, rightValue)) {
                return null;
            }
        }

        return new ValueChange(pair.getGlobalId(), property, leftValue, rightValue);
    }

    private boolean isIdProperty(NodePair nodePair, Property property){
        GlobalId globalId = nodePair.getGlobalId();

        if (globalId instanceof InstanceId) {
            return ((InstanceId) globalId).getCdoClass().getIdProperty().equals(property);
        }
        return false;
    }
}
