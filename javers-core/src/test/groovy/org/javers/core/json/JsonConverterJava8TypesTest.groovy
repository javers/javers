package org.javers.core.json

import com.google.gson.reflect.TypeToken
import org.javers.core.JaversBuilder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author bartosz.walacik
 */
class JsonConverterJava8TypesTest extends Specification {
    @Shared
    def jsonConverter = new JaversBuilder().withPrettyPrint(false).build().getJsonConverter()

    @Unroll
    def "should convert #expectedType (#givenValue) to and from JSON in ISO format"(){
        expect:
        jsonConverter.toJson( givenValue ) == expectedJson

        jsonConverter.fromJson( expectedJson, expectedType ) == givenValue

        where:
        expectedType << [LocalDateTime,
                         LocalDate,
                         new TypeToken<Optional<Integer>>(){}.type,
                         Optional
        ]
        givenValue   << [LocalDateTime.of( LocalDate.of(2001,01,31), LocalTime.of(15,14,13) ),
                         LocalDate.of(2001,01,31),
                         Optional.of(5),
                         Optional.empty()
        ]
        expectedJson << ['"2001-01-31T15:14:13"',
                         '"2001-01-31"',
                         '{"value":5}',
                         '{"value":null}',
        ]
    }
}
