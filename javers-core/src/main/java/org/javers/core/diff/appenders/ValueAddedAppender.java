package org.javers.core.diff.appenders;

import org.javers.common.collections.Collections;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ValueAdded;
import org.javers.core.metamodel.property.Property;
import org.javers.model.mapping.type.CollectionType;
import org.javers.model.mapping.type.JaversType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.javers.common.collections.Collections.difference;

/**
 * @author bartosz walacik
 * @author pawel szymczyk
 */
public class ValueAddedAppender extends PropertyChangeAppender<ValueAdded> {

    //TODO in fact it should be Collection<PrimitiveOrValueType>
    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return CollectionType.class;
    }

    @Override
    public Collection<ValueAdded> calculateChanges(NodePair pair, Property property) {
        Collection<Object> leftValues = Collections.asCollection(pair.getLeftPropertyValue(property));
        Collection<Object> rightValues = Collections.asCollection(pair.getRightPropertyValue(property));

        List<ValueAdded> added = new ArrayList<>();

        for (Object addedValue : difference(rightValues, leftValues)) {
            added.add(new ValueAdded(pair.getGlobalCdoId(), property, addedValue));
        }

        return added;
    }
}
