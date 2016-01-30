package org.javers.core.json.typeadapter.date;

import org.javers.core.json.BasicStringTypeAdapter;

import java.sql.Date;


/**
 * Serializes java.sql.Date to JSON String using ISO date format yyyy-MM-dd'T'HH:mm:ss.SSS
 *
 * @author bartosz walacik
 */
class JavaSqlDateTypeAdapter extends BasicStringTypeAdapter<Date> {

    @Override
    public String serialize(Date sourceValue) {
        return DateTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public Date deserialize(String serializedValue) {
        return new Date(DateTypeCoreAdapters.deserialize(serializedValue).toDate(DateTypeCoreAdapters.UTC).getTime());
    }

    @Override
    public Class getValueType() {
        return Date.class;
    }
}
