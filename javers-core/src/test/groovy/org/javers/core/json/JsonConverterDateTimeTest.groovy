package org.javers.core.json

import org.joda.time.DateTimeZone
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.util.logging.Logger

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
class JsonConverterDateTimeTest extends Specification {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JsonConverterDateTimeTest.class);

    @Shared
    def jsonConverter = javersTestAssembly().jsonConverter

    @Shared
    long time = 1443807427050

    @Unroll
    def "should convert #expectedType to and from JSON (#expectedJson) in ISO format"() {
        expect:
        logger.info ( "date "+givenValue.toString() +" converted to:" + jsonConverter.toJson(givenValue)+", expected:"+expectedJson)
        jsonConverter.toJson(givenValue) == expectedJson
        jsonConverter.fromJson(expectedJson, expectedType) == givenValue
        jsonConverter.fromJson(jsonConverter.toJson(givenValue), expectedType) == givenValue

        where:
        expectedType << [java.util.Date,
                         java.sql.Date,
                         java.sql.Timestamp,
                         java.sql.Time,
                         org.joda.time.LocalDateTime,
                         org.joda.time.LocalDate
        ]
        givenValue <<   [new java.util.Date(time),
                         new java.sql.Date(time),
                         new java.sql.Timestamp(time),
                         new java.sql.Time(time),
                         new org.joda.time.LocalDateTime(time, DateTimeZone.UTC),
                         new org.joda.time.LocalDate(2015,10,02)
        ]
        expectedJson << ['"2015-10-02T17:37:07.050"'] *5 +
                        ['"2015-10-02"']
    }

    def "should deserialize org.joda.time.LocalDateTime from legacy format"(){
      given:
      def noMillisDate = new org.joda.time.LocalDateTime(time-50)

      expect:
      jsonConverter.fromJson('"2015-10-02T19:37:07"', org.joda.time.LocalDateTime) == noMillisDate
    }
}
