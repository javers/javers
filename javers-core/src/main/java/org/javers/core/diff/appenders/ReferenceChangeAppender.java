package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.domain.changeType.ReferenceChanged;
import org.javers.model.mapping.Entity;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.ManagedClass;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.CollectionType;
import org.javers.model.mapping.type.EntityReferenceType;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.object.graph.ObjectNode;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.javers.common.validation.Validate.argumentCheck;

/**
 * @author bartosz walacik
 * @author pawel szymczyk
 */
public class ReferenceChangeAppender extends PropertyChangeAppender<ReferenceChanged> {

    private EntityManager entityManager;

    public ReferenceChangeAppender(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    protected Set<Class<JaversType>> getSupportedPropertyTypes() {
        return ENTITY_REF_TYPES;
    }

    @Override
    public Collection<ReferenceChanged> calculateChanges(NodePair pair, Property supportedProperty) {
        ObjectNode left = pair.getLeft();
        ObjectNode right =pair.getRight();

        Object leftReference = left.getPropertyValue(supportedProperty);
        Object rightReference = right.getPropertyValue(supportedProperty);
        ManagedClass leftManagedClass = entityManager.getByClass(leftReference.getClass());
        ManagedClass rightManagedClass = entityManager.getByClass(rightReference.getClass());

        if (leftReference == rightReference) {
            return Collections.EMPTY_SET;
        }

        return Sets.asSet(new ReferenceChanged(pair.getGlobalCdoId(),
                            supportedProperty,
                            leftReference,
                            rightReference));
    }
}
