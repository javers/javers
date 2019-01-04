package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author bartosz.walacik
 */
class InstantTypeAdapter extends BasicStringTypeAdapter<Instant> {
    private final DateTimeFormatter formatter = DateTimeFormatter.
            ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneId.of("UTC"));

    @Override
    public String serialize(Instant sourceValue) {
        return formatter.format(sourceValue);
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
