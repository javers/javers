package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;
import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;

import java.time.ZonedDateTime;

/**
 * @author bartosz.walacik
 */
class ZonedDateTimeTypeAdapter extends BasicStringTypeAdapter<ZonedDateTime> {

    @Override
    public String serialize(ZonedDateTime sourceValue) {
        return UtilTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public ZonedDateTime deserialize(String serializedValue) {
        return UtilTypeCoreAdapters.deserializeToZonedDateTime(serializedValue);
    }


    @Override
    public Class getValueType() {
        return ZonedDateTime.class;
    }
}
