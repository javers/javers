package org.javers.core.json.typeadapter.util;

import org.javers.common.collections.Lists;
import org.javers.core.json.JsonTypeAdapter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * @author bartosz.walacik
 */
public class UtilTypeCoreAdapters {
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static LocalDateTime deserialize(String date) {
        return LocalDateTime.parse(date, ISO_FORMAT);
    }

    public static Instant deserializeToInstant(String date) {
        return deserialize(date).toInstant(ZoneOffset.UTC);
    }

    public static String serialize(LocalDateTime date) {
        return date.format(ISO_FORMAT);
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

    public static String serialize(Date date) {
        return serialize(fromUtilDate(date));
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
