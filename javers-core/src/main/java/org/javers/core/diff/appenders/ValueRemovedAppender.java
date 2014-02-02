package org.javers.core.diff.appenders;

import org.javers.common.collections.Collections;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ValueRemoved;
import org.javers.core.metamodel.property.Property;
import org.javers.model.mapping.type.CollectionType;
import org.javers.model.mapping.type.JaversType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.javers.common.collections.Collections.difference;

/**
 * @author bartosz walacik
 * @author pawel szymczyk
 */
public class ValueRemovedAppender extends PropertyChangeAppender<ValueRemoved> {

    //TODO in fact it should be Collection<PrimitiveOrValueType>
    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return CollectionType.class;
    }

    @Override
    public Collection<ValueRemoved> calculateChanges(NodePair pair, Property property) {
        Collection<Object> leftValues = Collections.asCollection(pair.getLeftPropertyValue(property));
        Collection<Object> rightValues = Collections.asCollection(pair.getRightPropertyValue(property));

        Set<ValueRemoved> removedValues = new HashSet<>();

        for (Object addedValue : difference(leftValues, rightValues)) {
            removedValues.add(new ValueRemoved(pair.getGlobalCdoId(), property, addedValue));
        }

        return removedValues;
    }

}
