package org.javers.jodasupport;

import org.javers.core.ConditionalTypesPlugin;
import org.javers.core.JaversBuilder;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class JodaAddOns extends ConditionalTypesPlugin {

    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = ISODateTimeFormat.dateHourMinuteSecondMillis();

    static String serialize(LocalDateTime date) {
        return ISO_DATE_TIME_FORMATTER.print(date);
    }

    static LocalDateTime deserialize(String serializedValue) {
        if (serializedValue == null) {
            return null;
        }
        if (serializedValue.length() == 19) {
            return deserialize(serializedValue + ".0");
        }
        return ISO_DATE_TIME_FORMATTER.parseLocalDateTime(serializedValue);
    }

    @Override
    public void beforeAssemble(JaversBuilder javersBuilder) {
        javersBuilder.registerValueTypeAdapter(new LocalDateTimeTypeAdapter());
        javersBuilder.registerValueTypeAdapter(new LocalDateTypeAdapter());
    }
}