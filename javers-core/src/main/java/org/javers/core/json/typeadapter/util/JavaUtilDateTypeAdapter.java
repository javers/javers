package org.javers.core.json.typeadapter.util;

import org.javers.core.json.BasicStringTypeAdapter;

import java.time.ZoneId;
import java.util.Date;

/**
 * Serializes java.util.Date to JSON String using ISO util format yyyy-MM-dd'T'HH:mm:ss.SSS
 *
 * @author bartosz walacik
 */
class JavaUtilDateTypeAdapter extends BasicStringTypeAdapter<Date> {

    @Override
    public String serialize(Date sourceValue) {
        return UtilTypeCoreAdapters.serialize(sourceValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() );
    }

    @Override
    public Date deserialize(String serializedValue) {
        return UtilTypeCoreAdapters.deserializeToUtilDate(serializedValue);
    }

    @Override
    public Class getValueType() {
        return Date.class;
    }
}
