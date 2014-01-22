package org.javers.core.diff.appenders;

import org.javers.core.diff.NodePair;
import org.javers.core.diff.RealNodePair;
import org.javers.core.diff.changetype.ReferenceRemoved;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.CollectionType;
import org.javers.model.mapping.type.JaversType;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class ReferenceRemovedAppender extends PropertyChangeAppender<ReferenceRemoved> {

    //TODO in fact it should be Collection<EntityReferenceType>
    @Override
    protected Class<? extends JaversType> getSupportedPropertyType() {
        return CollectionType.class;
    }

    @Override
    public Collection<ReferenceRemoved> calculateChanges(NodePair pair, Property supportedProperty) {
        throw new IllegalStateException("not implemented");
    }
}