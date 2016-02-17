package org.javers.core.json.typeadapter.date;

import org.javers.common.collections.Lists;
import org.javers.core.json.JsonTypeAdapter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

/**
 * @author bartosz.walacik
 */
public class DateTypeCoreAdapters {
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = ISODateTimeFormat.dateHourMinuteSecondMillis();
    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER_LEGACY = ISODateTimeFormat.dateHourMinuteSecond();

    public static String serialize(LocalDateTime date) {
        return ISO_DATE_TIME_FORMATTER.print(date);
    }

    public static String serialize(DateTime date) {
        return ISO_DATE_TIME_FORMATTER.print(date);
    }

    public static String serializeToLocal(java.util.Date date) {
        return serialize(new DateTime(date.getTime()));
    }

    public static String serialize(java.util.Date date) {
        return serialize(new DateTime(date.getTime(), DateTimeZone.UTC));
    }

    public static LocalDateTime deserialize(String serializedValue) {
        if (serializedValue == null) {
            return null;
        }
        if (serializedValue.length() == 19) {
            return ISO_DATE_TIME_FORMATTER_LEGACY.parseLocalDateTime(serializedValue);
        }
        return ISO_DATE_TIME_FORMATTER.parseLocalDateTime(serializedValue);
    }

    public static List<JsonTypeAdapter> adapters() {
        return (List) Lists.immutableListOf(
                new LocalDateTimeTypeAdapter(),
                new LocalDateTypeAdapter(),
                new JavaUtilDateTypeAdapter(),
                new JavaSqlDateTypeAdapter(),
                new JavaSqlTimestampTypeAdapter(),
                new JavaSqlTimeTypeAdapter()
        );
    }
}
