package org.javers.core.json.typeadapter;

import org.javers.core.json.BasicStringTypeAdapter;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Serializes LocalDate to JSON String using ISO date format yyyy-MM-dd,
 * for ex. 2001-12-01
 *
 * @author bartosz walacik
 */
public class LocalDateTypeAdapter extends BasicStringTypeAdapter<LocalDate> {
    public static final DateTimeFormatter ISO_FORMATTER = ISODateTimeFormat.date();

    @Override
    public String serialize(LocalDate sourceValue) {
        return ISO_FORMATTER.print(sourceValue);
    }

    @Override
    public LocalDate deserialize(String serializedValue) {
        return ISO_FORMATTER.parseLocalDate(serializedValue);
    }

    @Override
    public Class getValueType() {
        return LocalDate.class;
    }
}
