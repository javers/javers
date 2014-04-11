package org.javers.core.metamodel.object;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;

import java.util.Map;

/**
 * Captured state of client's domain object.
 * Values and primitives are stored 'by value',
 * Referenced Entities and ValueObjects are stored 'by reference' using {@link GlobalCdoId}
 * <br/>
 *
 * Immutable
 *
 * @author bartosz walacik
 */
public class CdoSnapshot extends Cdo {
    private final Map<Property, Object> state;

    /**
     * should be assembled by {@link CdoSnapshotBuilder}
     */
    CdoSnapshot(GlobalCdoId globalId, Map<Property, Object> state) {
        super(globalId);
        Validate.argumentIsNotNull(state);
        this.state = state;
    }

    /**
     * @return {@link Optional#EMPTY}
     */
    @Override
    public Optional<Object> getWrappedCdo() {
        return Optional.empty();
    }

    @Override
    public Object getPropertyValue(Property property) {
        Validate.argumentIsNotNull(property);
        return state.get(property);
    }

    @Override
    public boolean isNull(Property property) {
        Validate.argumentIsNotNull(property);
        return !state.containsKey(property);
    }

    public boolean stateEquals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CdoSnapshot other = (CdoSnapshot) o;
        return this.state.equals(other.state);
    }
}
