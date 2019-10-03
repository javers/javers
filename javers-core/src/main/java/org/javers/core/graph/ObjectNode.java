package org.javers.core.graph;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.ValueObjectType;

import java.util.List;
import java.util.Optional;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * Node in client's domain object graph. Reflects one {@link Cdo} or {@link CdoSnapshot}.
 * <p/>
 * Cdo could be an {@link EntityType} or a {@link ValueObjectType}
 * <p/>
 * Implementation should delegate equals() and hashCode() to {@link Cdo}
 *
 * @author bartosz walacik
 */
public abstract class ObjectNode<T extends Cdo> {
    private final T cdo;

    public ObjectNode(T cdo) {
        argumentsAreNotNull(cdo);
        this.cdo = cdo;
    }

    /**
     * @return returns {@link Optional#empty()} for snapshots
     */
    public Optional<Object> wrappedCdo() {
        return cdo.getWrappedCdo();
    }

    /**
     * shortcut to {@link Cdo#getGlobalId()}
     */
    public GlobalId getGlobalId() {
        return cdo.getGlobalId();
    }

    /**
     * returns null if property is not ManagedType
     */
    public abstract GlobalId getReference(Property property);

    /**
     * returns null if property is not Collection of ManagedType
     */
    public abstract List<GlobalId> getReferences(JaversProperty property);

    protected abstract Object getDehydratedPropertyValue(String propertyName);

    public abstract Object getDehydratedPropertyValue(JaversProperty property);

    public Object getPropertyValue(Property property) {
        Validate.argumentIsNotNull(property);
        return cdo.getPropertyValue(property);
    }

    public boolean isNull(Property property){
        return cdo.isNull(property);
    }

    public ManagedType getManagedType() {
        return cdo.getManagedType();
    }

    public T getCdo() {
        return cdo;
    }

    public int cdoHashCode() {
        return cdo.hashCode();
    }
}