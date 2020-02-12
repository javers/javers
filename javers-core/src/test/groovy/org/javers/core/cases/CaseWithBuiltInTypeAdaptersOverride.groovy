package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.changetype.Atomic
import org.javers.core.json.BasicStringTypeAdapter
import spock.lang.Specification
import spock.lang.Unroll

import java.sql.Time
import java.sql.Timestamp
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.Period
import java.time.Year
import java.time.ZoneOffset
import java.time.ZonedDateTime

class LocalDateAdapter extends BasicStringTypeAdapter<LocalDate> {
    @Override
    Class getValueType() {
        return LocalDate.class
    }

    @Override
    String serialize(LocalDate value) {
        return String.valueOf(value.toEpochDay())
    }

    @Override
    LocalDate deserialize(String value) {
        return LocalDate.ofEpochDay(Long.valueOf(value))
    }
}

class LocalDateTimeAdapter extends BasicStringTypeAdapter<LocalDateTime> {
    @Override
    Class getValueType() {
        return LocalDateTime.class
    }

    @Override
    String serialize(LocalDateTime value) {
        return String.valueOf(value.toInstant(ZoneOffset.UTC).toEpochMilli())
    }

    @Override
    LocalDateTime deserialize(String value) {
        return Instant.ofEpochMilli(Long.valueOf(value)).atZone(ZoneOffset.UTC).toLocalDateTime()
    }
}

class LocalTimeAdapter extends BasicStringTypeAdapter<LocalTime> {
    @Override
    Class getValueType() {
        return LocalTime.class
    }

    @Override
    String serialize(LocalTime value) {
        return value.toEpochSecond(LocalDate.now(), ZoneOffset.UTC)
    }

    @Override
    LocalTime deserialize(String value) {
        return LocalTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(value)), ZoneOffset.UTC)
    }
}

class YearAdapter extends BasicStringTypeAdapter<Year> {
    @Override
    Class getValueType() {
        return Year.class
    }

    @Override
    String serialize(Year value) {
        return value.plusYears(1).toString()
    }

    @Override
    Year deserialize(String value) {
        return Year.parse(value).minusYears(1)
    }
}

class ZonedDateTimeAdapter extends BasicStringTypeAdapter<ZonedDateTime> {
    @Override
    Class getValueType() {
        return ZonedDateTime.class
    }

    @Override
    String serialize(ZonedDateTime value) {
        return String.valueOf(value.toEpochSecond())
    }

    @Override
    ZonedDateTime deserialize(String value) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(value)), ZoneOffset.UTC)
    }
}

class ZoneOffsetAdapter extends BasicStringTypeAdapter<ZoneOffset> {
    @Override
    Class getValueType() {
        return ZoneOffset.class
    }

    @Override
    String serialize(ZoneOffset value) {
        return "!" + value.toString()
    }

    @Override
    ZoneOffset deserialize(String value) {
        return ZoneOffset.of(value.substring(1))
    }
}

class OffsetDateTimeAdapter extends BasicStringTypeAdapter<OffsetDateTime> {
    @Override
    Class getValueType() {
        return OffsetDateTime.class
    }

    @Override
    String serialize(OffsetDateTime value) {
        return value.toEpochSecond()
    }

    @Override
    OffsetDateTime deserialize(String value) {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(value)), ZoneOffset.UTC)
    }
}

class InstantAdapter extends BasicStringTypeAdapter<Instant> {
    @Override
    Class getValueType() {
        return Instant.class
    }

    @Override
    String serialize(Instant value) {
        return String.valueOf(value.toEpochMilli())
    }

    @Override
    Instant deserialize(String value) {
        return Instant.ofEpochMilli(Long.valueOf(value))
    }
}

class PeriodAdapter extends BasicStringTypeAdapter<Period> {
    @Override
    Class getValueType() {
        return Period.class
    }

    @Override
    String serialize(Period value) {
        return "!" + value.toString()
    }

    @Override
    Period deserialize(String value) {
        return Period.parse(value.substring(1))
    }
}

class DurationAdapter extends BasicStringTypeAdapter<Duration> {
    @Override
    Class getValueType() {
        return Duration.class
    }

    @Override
    String serialize(Duration value) {
        return "!" + value.toString()
    }

    @Override
    Duration deserialize(String value) {
        return Duration.parse(value.substring(1))
    }
}

class DateAdapter extends BasicStringTypeAdapter<Date> {
    @Override
    Class getValueType() {
        return Date.class
    }

    @Override
    String serialize(Date value) {
        return String.valueOf(value.getTime())
    }

    @Override
    Date deserialize(String value) {
        return new Date(Long.valueOf(value))
    }
}

class SqlDateAdapter extends BasicStringTypeAdapter<java.sql.Date> {
    @Override
    Class getValueType() {
        return java.sql.Date.class
    }

    @Override
    String serialize(java.sql.Date value) {
        return String.valueOf(value.getTime())
    }

    @Override
    java.sql.Date deserialize(String value) {
        return new java.sql.Date(Long.valueOf(value))
    }
}

class TimestampAdapter extends BasicStringTypeAdapter<Timestamp> {
    @Override
    Class getValueType() {
        return Timestamp.class
    }

    @Override
    String serialize(Timestamp value) {
        return String.valueOf(value.getTime())
    }

    @Override
    Timestamp deserialize(String value) {
        return new Timestamp(Long.valueOf(value))
    }
}

class TimeAdapter extends BasicStringTypeAdapter<Time> {
    @Override
    Class getValueType() {
        return Time.class
    }

    @Override
    String serialize(Time value) {
        return String.valueOf(value.getTime())
    }

    @Override
    Time deserialize(String value) {
        return new Time(Long.valueOf(value))
    }
}

class FileAdapter extends BasicStringTypeAdapter<File> {
    @Override
    Class getValueType() {
        return File.class
    }

    @Override
    String serialize(File value) {
        return "!" + value.toString()
    }

    @Override
    File deserialize(String value) {
        return new File(value.substring(1))
    }
}

class UUIDAdapter extends BasicStringTypeAdapter<UUID> {
    @Override
    Class getValueType() {
        return UUID.class
    }

    @Override
    String serialize(UUID value) {
        return "!" + value.toString()
    }

    @Override
    UUID deserialize(String value) {
        return UUID.fromString(value.substring(1))
    }
}

class AtomicAdapter extends BasicStringTypeAdapter<Atomic> {
    @Override
    Class getValueType() {
        return Atomic.class
    }

    @Override
    String serialize(Atomic value) {
        return "!" + value.unwrap().toString()
    }

    @Override
    Atomic deserialize(String value) {
        return new Atomic(value.substring(1))
    }
}


class CaseWithBuiltInTypeAdaptersOverride extends Specification {
    @Unroll
    def "should support custom JsonTypeAdapter for #adapter.getValueType()"() {
        given:
        def converter = JaversBuilder.javers().registerValueTypeAdapter(adapter).build().getJsonConverter()
        def defaultConverter = JaversBuilder.javers().build().getJsonConverter()

        expect:
        converter.toJson(obj) == "\"" + adapter.serialize(obj) + "\""
        converter.fromJson(adapter.serialize(obj), adapter.getValueType()) == adapter.deserialize(adapter.serialize(obj))
        converter.toJson(obj) != defaultConverter.toJson(obj)

        where:
        adapter << [
                new LocalDateAdapter(),
                new LocalDateTimeAdapter(),
                new LocalTimeAdapter(),
                new YearAdapter(),
                new ZonedDateTimeAdapter(),
                new ZoneOffsetAdapter(),
                new OffsetDateTimeAdapter(),
                new InstantAdapter(),
                new PeriodAdapter(),
                new DurationAdapter(),
                new DateAdapter(),
                new SqlDateAdapter(),
                new TimestampAdapter(),
                new TimeAdapter(),
                new FileAdapter(),
                new UUIDAdapter(),
                new AtomicAdapter()
        ]

        obj << [
                LocalDate.now(),
                LocalDateTime.now(),
                LocalTime.now(),
                Year.now(),
                ZonedDateTime.now(),
                ZoneOffset.ofHours(0),
                OffsetDateTime.now(),
                Instant.now(),
                Period.ofDays(1),
                Duration.ofHours(1),
                new Date(),
                new java.sql.Date(2020, 1, 1),
                Timestamp.from(Instant.now()),
                new Time(1, 1, 1),
                new File("file"),
                UUID.randomUUID(),
                new Atomic("test")
        ]
    }
}
