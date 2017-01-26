package org.javers.core.json.typeadapter.util;

import org.javers.core.json.BasicStringTypeAdapter;

import java.sql.Date;
import java.sql.Timestamp;


/**
 * Serializes java.sql.Timestamp to JSON String using ISO util format yyyy-MM-dd'T'HH:mm:ss.SSS
 *
 * @author bartosz walacik
 */
class JavaSqlTimestampTypeAdapter extends BasicStringTypeAdapter<Timestamp> {

    @Override
    public String serialize(Timestamp sourceValue) {
        return UtilTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public Timestamp deserialize(String serializedValue) {
        return new Timestamp(java.util.Date.from(UtilTypeCoreAdapters.deserializeToInstant(serializedValue)).getTime());
    }

    @Override
    public Class getValueType() {
        return Timestamp.class;
    }
}
