package org.javers.core.json.typeadapter.date;

import org.javers.core.json.BasicStringTypeAdapter;

import java.sql.Time;


/**
 * Serializes java.sql.Time to JSON String using ISO date format yyyy-MM-dd'T'HH:mm:ss.SSS
 *
 * @author bartosz walacik
 */
class JavaSqlTimeTypeAdapter extends BasicStringTypeAdapter<Time> {

    @Override
    public String serialize(Time sourceValue) {
        return DateTypeAdapters.serialize(sourceValue);
    }

    @Override
    public Time deserialize(String serializedValue) {
        return new Time(DateTypeAdapters.deserialize(serializedValue).toDate().getTime());
    }

    @Override
    public Class getValueType() {
        return Time.class;
    }
}
