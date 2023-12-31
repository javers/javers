package org.javers.core.json.typeadapter.util;

import org.javers.common.collections.Lists;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.json.AbstractJsonTypeAdapter;
import org.javers.core.json.BasicStringTypeAdapter;
import org.javers.core.json.JsonAdvancedTypeAdapter;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.metamodel.type.ValueType;
import org.javers.java8support.OptionalTypeAdapter;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<AbstractJsonTypeAdapter> adapters() {
        return Lists.immutableListOf(
                new PathTypeAdapter(),
                new JavaUtilDateTypeAdapter(),
                new JavaSqlDateTypeAdapter(),
                new JavaSqlTimestampTypeAdapter(),
                new JavaSqlTimeTypeAdapter(),
                new FileTypeAdapter(),
                new UUIDTypeAdapter()
                );
    }

    public static List<ValueType> valueTypes() {
        return adapters()
                .stream()
                .flatMap(it -> getValueTypes(it).stream())
                .map(c -> new ValueType(c))
                .collect(Collectors.toList());
    }

    private static List<Class<?>> getValueTypes(AbstractJsonTypeAdapter adapter) {
        if (adapter instanceof JsonAdvancedTypeAdapter) {
            return List.of( ((JsonAdvancedTypeAdapter) adapter).getTypeSuperclass());
        }
        if (adapter instanceof JsonTypeAdapter) {
            return ((JsonTypeAdapter) adapter).getValueTypes();
        }
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
    }
}
