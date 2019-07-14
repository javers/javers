package org.javers.core.json.typeadapter.util;

import org.javers.core.json.BasicStringTypeAdapter;

import java.sql.Date;


/**
 * Serializes java.sql.Date to JSON String using ISO util format yyyy-MM-dd'T'HH:mm:ss.SSS
 *
 * @author bartosz walacik
 */
class JavaSqlDateTypeAdapter extends BasicStringTypeAdapter<Date> {

    @Override
    public String serialize(Date sourceValue) {
        return UtilTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public Date deserialize(String serializedValue) {
        return new Date(UtilTypeCoreAdapters.deserializeToUtilDate(serializedValue).getTime());
    }

    @Override
    public Class getValueType() {
        return Date.class;
    }
}
