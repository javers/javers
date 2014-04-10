package org.javers.core.metamodel.object;

import org.javers.common.collections.Optional;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;

import java.util.HashMap;
import java.util.Map;

import static org.javers.common.exception.exceptions.JaversExceptionCode.SNAPSHOT_STATE_VIOLATION;

/**
 * Captured state of client's domain object.
 * Values and primitives are stored 'by value',
 * Referenced Entities and ValueObjects are stored 'by reference' using {@link GlobalCdoId}
 *
 * @author bartosz walacik
 */
public class CdoSnapshot extends Cdo {
    private Map<Property, Object> state;

    public CdoSnapshot(GlobalCdoId globalId) {
        super(globalId);
        state = new HashMap<>();
    }

    public void addPropertyValue(Property property, Object value){
        Validate.argumentIsNotNull(property);
        if (value == null){
            return;
        }

        if (state.containsKey(property)){
            throw new JaversException(SNAPSHOT_STATE_VIOLATION);
        }

        state.put(property, value);
    }

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
}
