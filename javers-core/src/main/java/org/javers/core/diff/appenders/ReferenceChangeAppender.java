package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.changeType.ReferenceChanged;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.object.graph.ObjectNode;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * @author bartosz walacik
 * @author pawel szymczyk
 */
public class ReferenceChangeAppender extends PropertyChangeAppender<ReferenceChanged> {

    @Override
    protected Set<Class<JaversType>> getSupportedPropertyTypes() {
        return ENTITY_REF_TYPES;
    }

    @Override
    public Collection<ReferenceChanged> calculateChanges(NodePair pair, Property supportedProperty) {
        ObjectNode left = pair.getLeft();
        ObjectNode right =pair.getRight();

        if (!left.isEntity(supportedProperty)) {
            return Collections.EMPTY_SET;
        }

        Object leftReference = left.getPropertyValue(supportedProperty);
        Object rightReference = right.getPropertyValue(supportedProperty);

        if (leftReference == rightReference) {
            return Collections.EMPTY_SET;
        }

        GlobalCdoId leftEntity = left.getGlobalCdoIdOf(supportedProperty);
        GlobalCdoId rightEntity = right.getGlobalCdoIdOf(supportedProperty);

        return Sets.asSet(new ReferenceChanged(pair.getGlobalCdoId(),
                supportedProperty,
                leftEntity,
                rightEntity));
    }

}
