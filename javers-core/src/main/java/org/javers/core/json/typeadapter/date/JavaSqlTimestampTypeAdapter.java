package org.javers.core.json.typeadapter.date;

import org.javers.core.json.BasicStringTypeAdapter;

import java.sql.Timestamp;


/**
 * Serializes java.sql.Timestamp to JSON String using ISO date format yyyy-MM-dd'T'HH:mm:ss.SSS
 *
 * @author bartosz walacik
 */
class JavaSqlTimestampTypeAdapter extends BasicStringTypeAdapter<Timestamp> {

    @Override
    public String serialize(Timestamp sourceValue) {
        return DateTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public Timestamp deserialize(String serializedValue) {
        return new Timestamp(DateTypeCoreAdapters.deserialize(serializedValue).toDate(DateTypeCoreAdapters.UTC).getTime());
    }

    @Override
    public Class getValueType() {
        return Timestamp.class;
    }
}
