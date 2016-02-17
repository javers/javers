package org.javers.core.json

import com.google.gson.reflect.TypeToken
import org.javers.core.JaversBuilder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.Period
import java.time.Year
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

/**
 * @author bartosz.walacik
 */
class JsonConverterJava8TypesTest extends Specification {
    @Shared
    def jsonConverter = new JaversBuilder().withPrettyPrint(false).build().getJsonConverter()

    @Shared
    def localDate = LocalDateTime.of(2001,01,31,15,14,13,85*1000_000)

    @Unroll
    def "should convert #expectedType (#givenValue) to and from JSON"(){
        expect:
        jsonConverter.toJson( givenValue ) == expectedJson
        jsonConverter.fromJson( expectedJson, expectedType ) == givenValue

        where:
        expectedType << [LocalDateTime,
                         LocalDate,
                         LocalTime,
                         new TypeToken<Optional<Integer>>(){}.type,
                         Optional,
                         Year,
                         ZonedDateTime,
                         ZoneOffset,
                         OffsetDateTime,
                         Instant,
                         Period,
                         Duration
        ]
        givenValue   << [localDate,
                         LocalDate.of(2001,01,31),
                         LocalTime.of(12,15,31),
                         Optional.of(5),
                         Optional.empty(),
                         Year.of(2015),
                         ZonedDateTime.of(localDate, ZoneId.of("Europe/Berlin")),
                         ZoneOffset.of("+05:00"),
                         OffsetDateTime.of(localDate, ZoneOffset.of("+05:00")),
                         Instant.parse("2010-01-01T12:00:00Z"),
                         Period.between(LocalDate.of(2014, 1, 1), LocalDate.of(2015, 3, 7)),
                         Duration.ofSeconds(6005)
        ]
        expectedJson << ['"2001-01-31T15:14:13.085"',
                         '"2001-01-31"',
                         '"12:15:31"',
                         '{"value":5}',
                         '{"value":null}',
                         '"2015"',
                         '"2001-01-31T15:14:13.085+01:00[Europe/Berlin]"',
                         '"+05:00"',
                         '"2001-01-31T15:14:13.085+05:00"',
                         '"2010-01-01T12:00:00Z"',
                         '"P1Y2M6D"',
                         '"PT1H40M5S"'
        ]
    }
}
