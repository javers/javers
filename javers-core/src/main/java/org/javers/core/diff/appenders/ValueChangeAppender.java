package org.javers.core.diff.appenders;

import org.javers.common.collections.Objects;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.PrimitiveOrValueType;
import org.javers.core.metamodel.type.TypeMapper;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;

/**
 * @author bartosz walacik
 */
public class ValueChangeAppender extends PropertyChangeAppender<ValueChange> {

    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return PrimitiveOrValueType.class;
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

        return new ValueChange(pair.getGlobalCdoId(), property, leftValue, rightValue);
    }
}
