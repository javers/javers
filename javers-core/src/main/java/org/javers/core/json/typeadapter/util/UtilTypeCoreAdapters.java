package org.javers.core.json.typeadapter.util;

import org.javers.common.collections.Lists;
import org.javers.core.json.JsonTypeAdapter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @author bartosz.walacik
 */
public class UtilTypeCoreAdapters {
    private static final DateTimeFormatter ISO_INSTANT_FORMAT = DateTimeFormatter.ISO_INSTANT;
    private static final DateTimeFormatter ISO_LOCAL_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_TIME;
    private static final DateTimeFormatter ISO_LOCAL_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter ISO_ZONED_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    public static LocalDateTime deserializeLocalDateTime(String date) {
        return LocalDateTime.parse(date, ISO_LOCAL_FORMAT);
    }

    public static LocalTime deserializeLocalTime(String date) {
        return LocalTime.parse(date, ISO_LOCAL_TIME_FORMAT);
    }

    public static ZonedDateTime deserializeToZonedDateTime(String date) {
        return ZonedDateTime.parse(date, ISO_ZONED_FORMAT);
    }

    public static Instant deserializeToInstant(String date) {
        return Instant.parse(date);
    }

    public static Date deserializeToUtilDate(String date) {
        LocalDateTime localDateTime = UtilTypeCoreAdapters.deserializeLocalDateTime(date);
        return java.util.Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String serialize(Instant sourceValue) {
        return ISO_INSTANT_FORMAT.format(sourceValue);
    }

    public static String serialize(LocalDateTime date) {
        return date.format(ISO_LOCAL_FORMAT);
    }

    public static String serialize(ZonedDateTime date) {
        return date.format(ISO_ZONED_FORMAT);
    }

    public static String serialize(LocalTime date) {
        return date.format(ISO_LOCAL_TIME_FORMAT);
    }

    public static String serialize(Date date) {
        return serialize(fromUtilDate(date));
    }

    public static LocalDateTime fromUtilDate(Date date) {
        if (date.getClass() == Date.class) {
            return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }
        return fromUtilDate(new Date(date.getTime())); //hack for old java.sql.Date
    }

    public static Date toUtilDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static List<JsonTypeAdapter> adapters() {
        return (List) Lists.immutableListOf(
                new JavaUtilDateTypeAdapter(),
                new JavaSqlDateTypeAdapter(),
                new JavaSqlTimestampTypeAdapter(),
                new JavaSqlTimeTypeAdapter(),
                new FileTypeAdapter(),
                new UUIDTypeAdapter()
        );
    }
}
