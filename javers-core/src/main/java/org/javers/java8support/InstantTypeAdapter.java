package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * @author bartosz.walacik
 */
class InstantTypeAdapter extends BasicStringTypeAdapter<Instant> {
    private final DateTimeFormatter formatterMillis = DateTimeFormatter.
            ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneId.of("UTC"));

    private final DateTimeFormatter formatterSec = DateTimeFormatter.
            ofPattern("yyyy-MM-dd'T'HH:mm:ssX").withZone(ZoneId.of("UTC"));

    @Override
    public String serialize(Instant sourceValue) {
        if (sourceValue.getNano() == 0) {
            return formatterSec.format(sourceValue);
        }
        return formatterMillis.format(sourceValue);
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
