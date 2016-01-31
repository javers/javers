package org.javers.core.json.typeadapter.date;

import org.javers.core.json.BasicStringTypeAdapter;
import org.joda.time.LocalDateTime;


/**
 * Serializes LocalDateTime to JSON String using ISO date format yyyy-MM-dd'T'HH:mm:ss.SSS
 * <br><br>
 *
 * Without typeAdapter, LocalDateTime written to JSON would be:
 * {"iLocalMillis":1007245380000,"iChronology":{"iBase":{"iMinDaysInFirstWeek":4}}}
 *
 * @author bartosz walacik
 */
class LocalDateTimeTypeAdapter extends BasicStringTypeAdapter<LocalDateTime> {

    @Override
    public String serialize(LocalDateTime sourceValue) {
        return DateTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public LocalDateTime deserialize(String serializedValue) {
        return DateTypeCoreAdapters.deserialize(serializedValue);
    }

    @Override
    public Class getValueType() {
        return LocalDateTime.class;
    }
}
