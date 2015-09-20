package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;

import java.time.Instant;

/**
 * @author bartosz.walacik
 */
class InstantTypeAdapter extends BasicStringTypeAdapter<Instant> {
    @Override
    public String serialize(Instant sourceValue) {
        return sourceValue.toString();
    }

    @Override
    public Instant deserialize(String serializedValue) {
        return Instant.parse(serializedValue);
    }

    @Override
    public Class getValueType() {
        return Instant.class;
    }
}
