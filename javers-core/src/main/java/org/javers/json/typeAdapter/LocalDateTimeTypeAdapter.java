package org.javers.json.typeAdapter;

import org.javers.json.BasicStringTypeAdapter;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Serializes LocalDateTime to JSON String using ISO date format yyyy-MM-dd'T'HH:mm,
 * for ex. "2001-12-01T22:23"
 *
 * @author bartosz walacik
 */
public class LocalDateTimeTypeAdapter extends BasicStringTypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter ISO_FORMATTER = ISODateTimeFormat.dateHourMinuteSecond();

    @Override
    public String serialize(LocalDateTime sourceValue) {
        return ISO_FORMATTER.print(sourceValue);
    }

    @Override
    public LocalDateTime deserialize(String serializedValue) {
        return ISO_FORMATTER.parseLocalDateTime(serializedValue);
    }

    @Override
    public Class<LocalDateTime> getType() {
        return LocalDateTime.class;
    }
}
