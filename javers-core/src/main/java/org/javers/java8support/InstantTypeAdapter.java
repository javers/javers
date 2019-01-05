package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;
import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;

import java.time.Instant;

/**
 * @author bartosz.walacik
 */
class InstantTypeAdapter extends BasicStringTypeAdapter<Instant> {
    @Override
    public String serialize(Instant sourceValue) {
        return UtilTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public Instant deserialize(String serializedValue) {
        return UtilTypeCoreAdapters.deserializeToInstant(serializedValue);
    }

    @Override
    public Class getValueType() {
        return Instant.class;
    }
}
