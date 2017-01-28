package org.javers.jodasupport;

import org.javers.core.json.BasicStringTypeAdapter;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Serializes LocalDate to JSON String using ISO util format yyyy-MM-dd
 *
 * @author bartosz walacik
 */
class LocalDateTypeAdapter extends BasicStringTypeAdapter<LocalDate> {
    private static final DateTimeFormatter ISO_DATE_FORMATTER = ISODateTimeFormat.date();

    @Override
    public String serialize(LocalDate sourceValue) {
        return ISO_DATE_FORMATTER.print(sourceValue);
    }

    @Override
    public LocalDate deserialize(String serializedValue) {
        return ISO_DATE_FORMATTER.parseLocalDate(serializedValue);
    }

    @Override
    public Class getValueType() {
        return LocalDate.class;
    }
}
