package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ValueAdded;
import org.javers.core.metamodel.property.Property;
import org.javers.model.mapping.type.CollectionType;
import org.javers.model.mapping.type.JaversType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.javers.common.collections.Arrays.asList;
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
        Collection<Object> leftValues = getValues(pair.getLeftPropertyValue(property));
        Collection<Object> rightValues = getValues(pair.getRightPropertyValue(property));

        Set<ValueAdded> added = new HashSet<>();

        for (Object addedValue : difference(rightValues, leftValues)) {
            added.add(new ValueAdded(pair.getGlobalCdoId(), property, addedValue));
        }

        return added;
    }

    private Collection<Object> getValues(Object values) {
        if (values.getClass().isArray()) {
            return asList(values);
        } else {
            return (Collection<Object>) values;
        }
    }
}
