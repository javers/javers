package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.model.domain.changeType.ReferenceChanged;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.CollectionType;
import org.javers.model.mapping.type.EntityReferenceType;
import org.javers.model.mapping.type.JaversType;

import java.util.Collection;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class ReferenceChangeAppender extends PropertyChangeAppender<ReferenceChanged> {

    @Override
    protected Set<Class<JaversType>> getSupportedPropertyTypes() {
        return ENTITY_REF_TYPES;
    }

    @Override
    public Collection<ReferenceChanged> calculateChanges(NodePair pair, Property supportedProperty) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
