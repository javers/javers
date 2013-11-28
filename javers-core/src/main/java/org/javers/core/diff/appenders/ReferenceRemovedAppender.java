package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.model.domain.changeType.ReferenceRemoved;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.JaversType;

import java.util.Collection;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class ReferenceRemovedAppender extends PropertyChangeAppender<ReferenceRemoved> {

    //TODO in fact it should be Collection<EntityReferenceType>
    @Override
    protected Set<Class<JaversType>> getSupportedPropertyTypes() {
        return COLLECTION_TYPES;
    }

    @Override
    public Collection<ReferenceRemoved> calculateChanges(NodePair pair, Property supportedProperty) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}