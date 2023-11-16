package org.javers.java8support;

import java.time.OffsetTime;
import org.javers.core.json.BasicStringTypeAdapter;

class OffsetTimeTypeAdapter extends BasicStringTypeAdapter<OffsetTime> {
    @Override
    public String serialize(OffsetTime sourceValue) {
        return sourceValue.toString();
    }

    @Override
    public OffsetTime deserialize(String serializedValue) {
        return OffsetTime.parse(serializedValue);
    }

    @Override
    public Class getValueType() {
        return OffsetTime.class;
    }
}
