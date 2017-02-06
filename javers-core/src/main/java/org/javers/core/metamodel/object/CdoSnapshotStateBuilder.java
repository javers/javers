package org.javers.core.metamodel.object;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Property;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bartosz walacik
 */
public class CdoSnapshotStateBuilder {
    private final Map<String, Object> properties = new HashMap<>();

    private CdoSnapshotStateBuilder(){
    }

    public static CdoSnapshotStateBuilder cdoSnapshotState(){
        return new CdoSnapshotStateBuilder();
    }

    public CdoSnapshotStateBuilder withPropertyValue(Property property, Object value){
        Validate.argumentIsNotNull(property);
        if (value == null){
            return this;
        }

        if (properties.containsKey(property.getName())){
            throw new JaversException(JaversExceptionCode.SNAPSHOT_STATE_VIOLATION);
        }

        properties.put(property.getName(), value);
        return this;
    }

    public CdoSnapshotState build() {
        return new CdoSnapshotState(properties);
    }
}
