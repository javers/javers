package org.javers.core.json.typeadapter.util;

import org.javers.core.json.BasicStringTypeAdapter;

import java.sql.Timestamp;
import java.time.LocalDateTime;


/**
 * Serializes java.sql.Timestamp to an ISO local date-time JSON string, preserving nanoseconds.
 *
 * @author bartosz walacik
 */
class JavaSqlTimestampTypeAdapter extends BasicStringTypeAdapter<Timestamp> {

    @Override
    public String serialize(Timestamp sourceValue) {
        return UtilTypeCoreAdapters.serialize(sourceValue.toLocalDateTime());
    }

    @Override
    public Timestamp deserialize(String serializedValue) {
        LocalDateTime date = UtilTypeCoreAdapters.deserializeLocalDateTime(serializedValue);
        return java.sql.Timestamp.valueOf(date);
    }

    @Override
    public Class getValueType() {
        return Timestamp.class;
    }
}
