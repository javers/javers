package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.ReferenceAdded;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.CollectionType;
import org.javers.core.metamodel.type.JaversType;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class ReferenceAddedAppender extends PropertyChangeAppender<ReferenceAdded> {

    //TODO in fact it should be Collection<EntityType>
    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return CollectionType.class;
    }

    @Override
    public Collection<ReferenceAdded> calculateChanges(NodePair pair, Property supportedProperty) {
        throw new IllegalStateException("not implemented");
    }
}
