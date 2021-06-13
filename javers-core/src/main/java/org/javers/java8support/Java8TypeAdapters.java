package org.javers.java8support;

import org.javers.common.collections.Lists;
import org.javers.core.diff.custom.CustomValueToStringTemplate;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.metamodel.clazz.ValueDefinition;
import org.javers.core.metamodel.type.ValueType;

import java.time.*;
import java.util.List;

public class Java8TypeAdapters {

    public static List<JsonTypeAdapter> adapters() {
        return (List) Lists.immutableListOf(
            new LocalDateTypeAdapter(),
            new LocalDateTimeTypeAdapter(),
            new LocalTimeTypeAdapter(),
            new YearTypeAdapter(),
            new ZonedDateTimeTypeAdapter(),
            new ZoneOffsetTypeAdapter(),
            new OffsetDateTimeTypeAdapter(),
            new InstantTypeAdapter(),
            new PeriodTypeAdapter(),
            new DurationTypeAdapter());
    }

    public static List<ValueType> valueTypes() {
        return (List) Lists.immutableListOf(
                buildValueType(LocalDate.class, v -> v.getYear() + "," + v.getMonthValue() + "," + v.getDayOfMonth()),
                buildValueType(LocalDateTime.class, v -> v.toString().replace("T", ",")),
                buildValueType(LocalTime.class, v -> v.getHour() + "," + v.getMinute() + "," + v.getSecond() + "," + v.getNano()),
                buildValueType(Year.class, v -> v.toString()),
                buildValueType(ZonedDateTime.class, v -> v.toLocalDateTime().toString() + "," + v.getOffset() + "," + v.getZone().getId()),
                buildValueType(ZoneOffset.class, v -> v.getTotalSeconds() + ""),
                buildValueType(OffsetDateTime.class, v -> v.toLocalDateTime().toString() + "," + v.getOffset()),
                buildValueType(Instant.class, v -> v.getEpochSecond() + "," + v.getNano()),
                buildValueType(Period.class, v -> v.getYears() + "," + v.getMonths() + "," + v.getDays()),
                buildValueType(Duration.class, v -> v.getSeconds() + "," + v.getNano())
        );
    }

    static <T> ValueType buildValueType(Class<T> clazz, CustomValueToStringTemplate<T> toString) {
        return new ValueType(clazz, toString);
    }
}