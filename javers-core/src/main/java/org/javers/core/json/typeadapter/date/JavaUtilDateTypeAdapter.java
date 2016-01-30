package org.javers.core.json.typeadapter.date;

import org.javers.core.json.BasicStringTypeAdapter;

import java.util.Date;


/**
 * Serializes java.util.Date to JSON String using ISO date format yyyy-MM-dd'T'HH:mm:ss.SSS
 *
 * @author bartosz walacik
 */
class JavaUtilDateTypeAdapter extends BasicStringTypeAdapter<Date> {

    @Override
    public String serialize(Date sourceValue) {
        return DateTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public Date deserialize(String serializedValue) {
        return DateTypeCoreAdapters.deserialize(serializedValue).toDate(DateTypeCoreAdapters.UTC);
    }

    @Override
    public Class getValueType() {
        return Date.class;
    }
}
