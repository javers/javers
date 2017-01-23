package org.javers.core.json.typeadapter.util;

import org.javers.common.collections.Lists;
import org.javers.core.json.JsonTypeAdapter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.LocalDateTime;

import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

/**
 * @author bartosz.walacik
 */
public class UtilTypeCoreAdapters {
    public static final ZoneId UTC = ZoneId.of("UTC");

    public static String serializeToLocal(java.util.Date date) {
        return serialize(new DateTime(date.getTime()));
    }

    public static String serialize(java.util.Date date) {
        date.toInstant().atZone(UTC).toLocalDateTime();

        //TODO date.toInstant ...
        return serialize(new DateTime(date.getTime(), DateTimeZone.UTC));
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
