package org.javers.core.diff.appenders;

import org.javers.common.collections.Objects;
import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.model.domain.changeType.ValueChange;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.PrimitiveType;
import org.javers.model.mapping.type.ValueObjectType;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class ValueChangeAppender extends PropertyChangeAppender<ValueChange> {

    @Override
    protected Set<Class<JaversType>> getSupportedPropertyTypes() {
        return VALUE_TYPES;
    }

    /**
     * @param property supported property (of PrimitiveType or ValueObjectType)
     */
    @Override
    public Collection<ValueChange> calculateChanges(NodePair pair, Property property) {
        Object leftValue = pair.getLeftPropertyValue(property);
        Object rightValue = pair.getRightPropertyValue(property);

        if (Objects.nullSafeEquals(leftValue,rightValue)) {
            return Collections.EMPTY_SET;
        }

        ValueChange change = new ValueChange(pair.getGlobalCdoId(), property, leftValue, rightValue);
        return  Sets.asSet(change);
    }
}
