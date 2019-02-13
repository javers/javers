package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;
import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;

import java.time.LocalTime;

/**
 * @author bartosz.walacik
 */
class LocalTimeTypeAdapter extends BasicStringTypeAdapter<LocalTime> {

    @Override
    public String serialize(LocalTime sourceValue) {
        return UtilTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public LocalTime deserialize(String serializedValue) {
        return UtilTypeCoreAdapters.deserializeLocalTime(serializedValue);
    }

    @Override
    public Class getValueType() {
        return LocalTime.class;
    }
}
