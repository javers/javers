package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.NodePair;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Property scope change appender,
 * follows Chain-of-responsibility pattern
 *
 * @author bartosz walacik
 */
public abstract class PropertyChangeAppender <T extends PropertyChange> {
    protected final static Set<Class<JaversType>> COLLECTION_TYPES = (Set) Sets.asSet(CollectionType.class);
    protected final static Set<Class<JaversType>> VALUE_TYPES = (Set) Sets.asSet(PrimitiveType.class, ValueObjectType.class);
    protected final static Set<Class<JaversType>> ENTITY_REF_TYPES = (Set) Sets.asSet(EntityReferenceType.class);

    /**
     * checks if given property is supported and if so,
     * delegates calculation to concrete appender in calculateChanges()
     */
    public final Collection<T> calculateChangesIfSupported(NodePair pair, Property property) {
        if (!supports(property)) {
             return Collections.EMPTY_SET;
        }
        return calculateChanges(pair, property);
    }

    protected boolean supports(Property property) {
        return getSupportedPropertyTypes().contains(property.getType().getClass());
    }

    protected abstract Set<Class<JaversType>> getSupportedPropertyTypes();

    protected abstract Collection<T> calculateChanges(NodePair pair, Property supportedProperty);
}
