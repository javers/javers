package org.javers.java8support;

import org.javers.common.collections.Lists;
import org.javers.core.json.JsonTypeAdapter;

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
}