package org.javers.core.diff.appenders;

import org.javers.common.collections.Objects;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.PrimitiveOrValueType;

/**
 * @author bartosz walacik
 */
class ValueChangeAppender extends PropertyChangeAppender<ValueChange> {

    @Override
    protected boolean supports(JaversType propertyType) {
        return  propertyType instanceof PrimitiveOrValueType;
    }

    /**
     * @param property supported property (of PrimitiveType or ValueObjectType)
     */
    @Override
    public ValueChange calculateChanges(NodePair pair, Property property) {
        Object leftValue = pair.getLeftPropertyValue(property);
        Object rightValue = pair.getRightPropertyValue(property);

        if (Objects.nullSafeEquals(leftValue,rightValue)) {
            return null;
        }

        return new ValueChange(pair.getGlobalId(), property, leftValue, rightValue);
    }
}
