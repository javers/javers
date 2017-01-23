package org.javers.core.json.typeadapter.util;

import org.javers.core.json.BasicStringTypeAdapter;

import java.sql.Time;

/**
 * Serializes java.sql.Time to JSON String using ISO util format yyyy-MM-dd'T'HH:mm:ss.SSS
 *
 * @author bartosz walacik
 */
class JavaSqlTimeTypeAdapter extends BasicStringTypeAdapter<Time> {

    @Override
    public String serialize(Time sourceValue) {
        return UtilTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public Time deserialize(String serializedValue) {
        return new Time(UtilTypeCoreAdapters.deserialize(serializedValue).toDate(UtilTypeCoreAdapters.UTC).getTime());
    }

    @Override
    public Class getValueType() {
        return Time.class;
    }
}
