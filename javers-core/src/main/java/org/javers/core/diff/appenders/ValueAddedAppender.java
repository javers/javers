package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.model.domain.changeType.ValueAdded;
import org.javers.model.domain.changeType.ValueRemoved;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.JaversType;

import java.util.Collection;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class ValueAddedAppender extends PropertyChangeAppender<ValueAdded> {

    //TODO in fact it should be Collection<PrimitiveType> or Collection<ValueObjectType>
    @Override
    protected Set<Class<JaversType>> getSupportedPropertyTypes() {
        return COLLECTION_TYPES;
    }
    @Override
    public Collection<ValueAdded> calculateChanges(NodePair pair, Property supportedProperty) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
