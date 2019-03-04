package org.javers.core.graph;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.object.ValueObjectIdWithHash;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.ManagedType;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Wrapper for live client's domain object (aka CDO)
 *
 * @author bartosz walacik
 */
abstract class LiveCdo extends Cdo {
    private GlobalId globalId;

    LiveCdo(GlobalId globalId, ManagedType managedType) {
        super(managedType);
        this.globalId = globalId;
    }

    void swapId(GlobalId globalId) {
        this.globalId = globalId;
    }

    @Override
    public GlobalId getGlobalId() {
        return globalId;
    }

    @Override
    public Object getPropertyValue(String propertyName) {
        argumentIsNotNull(propertyName);
        Property property = getManagedType().getProperty(propertyName);
        return getPropertyValue(property);
    }

    @Override
    public Object getPropertyValue(Property property) {
        argumentIsNotNull(property);
        return property.get(wrappedCdo());
    }

    /**
     * never returns empty
     */
    @Override
    public Optional<Object> getWrappedCdo() {
        return Optional.of(wrappedCdo());
    }

    @Override
    public boolean isNull(Property property) {
        argumentIsNotNull(property);
        return property.isNull(wrappedCdo());
    }

    abstract Object wrappedCdo();

    public boolean requiresObjectHasher() {
        return globalId instanceof ValueObjectIdWithHash;
    }
}
