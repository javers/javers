package org.javers.core.json.typeadapter.date;

import org.javers.core.json.BasicStringTypeAdapter;
import org.joda.time.DateTime;
import java.util.Date;


/**
 * Serializes java.util.Date to JSON String using ISO date format yyyy-MM-dd'T'HH:mm:ss.SSS
 *
 * @author bartosz walacik
 */
class JavaUtilDateTypeAdapter extends BasicStringTypeAdapter<Date> {

    @Override
    public String serialize(Date sourceValue) {
        DateTime jodaDate = new DateTime(sourceValue.getTime());
        return DateTypeAdapters.serialize(jodaDate);
    }

    @Override
    public Date deserialize(String serializedValue) {
        return DateTypeAdapters.deserialize(serializedValue).toDate();
    }

    @Override
    public Class getValueType() {
        return Date.class;
    }
}
