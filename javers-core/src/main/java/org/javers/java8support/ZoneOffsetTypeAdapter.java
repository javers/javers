package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;
import org.javers.core.json.JsonTypeAdapter;

import java.time.ZoneOffset;

/**
 * @author bartosz.walacik
 */
class ZoneOffsetTypeAdapter extends BasicStringTypeAdapter<ZoneOffset> {
    @Override
    public String serialize(ZoneOffset sourceValue) {
        return sourceValue.getId();
    }

    @Override
    public ZoneOffset deserialize(String serializedValue) {
        return ZoneOffset.of(serializedValue);
    }

    @Override
    public Class getValueType() {
        return ZoneOffset.class;
    }
}
