package org.javers.core.json.typeadapter;

import org.javers.core.json.BasicStringTypeAdapter;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import java.lang.reflect.Type;


/**
 * Serializes LocalDateTime to JSON String using ISO date format yyyy-MM-dd'T'HH:mm,
 * for ex. 2001-12-01T22:23
 * <br/><br/>
 *
 * Without typeAdapter, LocalDateTime written to JSON would be:
 * {"iLocalMillis":1007245380000,"iChronology":{"iBase":{"iMinDaysInFirstWeek":4}}}
 *
 * @author bartosz walacik
 */
public class LocalDateTimeTypeAdapter extends BasicStringTypeAdapter<LocalDateTime> {
    public static final DateTimeFormatter ISO_FORMATTER = ISODateTimeFormat.dateHourMinuteSecond();

    @Override
    public String serialize(LocalDateTime sourceValue) {
        return ISO_FORMATTER.print(sourceValue);
    }

    @Override
    public LocalDateTime deserialize(String serializedValue) {
        return ISO_FORMATTER.parseLocalDateTime(serializedValue);
    }

    @Override
    public Type getType() {
        return LocalDateTime.class;
    }
}
