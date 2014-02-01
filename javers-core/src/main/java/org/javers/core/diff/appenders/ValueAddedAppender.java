package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ValueAdded;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.CollectionType;
import org.javers.core.metamodel.type.JaversType;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class ValueAddedAppender extends PropertyChangeAppender<ValueAdded> {

    //TODO in fact it should be Collection<PrimitiveOrValueType>
    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return CollectionType.class;
    }

    @Override
    public Collection<ValueAdded> calculateChanges(NodePair pair, Property supportedProperty) {
        throw new IllegalStateException("not implemented");
    }
}
