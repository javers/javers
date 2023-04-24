package org.javers.core.json

import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.LocalDate
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

    @Shared
    int zoneOffsetHours = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).getOffset().getTotalSeconds()/3600

    @Shared
    long zoneOffsetMinutes = (Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).getOffset().getTotalSeconds() % 3600) / 60;

    @Unroll
    def "should convert java8.LocalDateTime from json #fromJson to json #expectedJson"(){
      when:
      def java8Time = jsonConverter.fromJson(fromJson, java.time.LocalDateTime)

      then:
      jsonConverter.toJson(java8Time) == expectedJson

      where:
      fromJson << [
                '"2015-10-02T17:37:07.050"',
                '"2015-10-02T17:37:07.05"',
                '"2015-10-02T17:37:07.000"',
                '"2015-10-02T17:37:07.0"',
                '"2015-10-02T17:37:07"'
              ]
      expectedJson << [
              '"2015-10-02T17:37:07.05"',
              '"2015-10-02T17:37:07.05"',
              '"2015-10-02T17:37:07"',
              '"2015-10-02T17:37:07"',
              '"2015-10-02T17:37:07"'
      ]
    }

    @Unroll
    def "should convert LocalDateTime from json #fromJson to json #expectedJson"(){
        when:
        def javaTime =  jsonConverter.fromJson(fromJson, java.time.LocalDateTime)

        then:
        jsonConverter.toJson(javaTime) == expectedJson

        where:
        fromJson << [
                '"2015-10-02T17:37:07.050"',
                '"2015-10-02T17:37:07.05"',
                '"2015-10-02T17:37:07.0"',
                '"2015-10-02T17:37:07"'
        ]
        expectedJson << [
                '"2015-10-02T17:37:07.05"',
                '"2015-10-02T17:37:07.05"',
                '"2015-10-02T17:37:07"',
                '"2015-10-02T17:37:07"'
        ]
    }

    @Unroll
    def "should convert #expectedType to and from JSON (#expectedJson) in ISO format"() {
        given:
        println "ZonedDateTime.now:   " + ZonedDateTime.now()
        println "time:                " + Instant.ofEpochMilli(time)
        println "zone offset:         " + zoneOffsetHours + "H " + zoneOffsetMinutes + "M"
        println "givenValue:          " + givenValue.toString() + " " + givenValue.getClass()
        println "expectedJson:        " + expectedJson
        println "converted to JSON:   " + jsonConverter.toJson(givenValue)
        println "converted from JSON: " + jsonConverter.fromJson(expectedJson, expectedType)

        expect:
        jsonConverter.toJson(givenValue) == expectedJson
        jsonConverter.fromJson(expectedJson, expectedType) == givenValue

        where:
        expectedType << [java.util.Date,
                         java.sql.Timestamp,
                         java.sql.Date,
                         java.sql.Time,
                         java.time.LocalDate
        ]
        givenValue <<   [new java.util.Date(time - zoneOffsetMinutes * 60 * 1000),
                         new java.sql.Timestamp(time - zoneOffsetMinutes * 60 * 1000),
                         new java.sql.Date(time - zoneOffsetMinutes * 60 * 1000),
                         new java.sql.Time(time - zoneOffsetMinutes * 60 * 1000),
                         LocalDate.of(2015,10,02)
        ]
        expectedJson << [
                        '"2015-10-02T'+(17+zoneOffsetHours)+':37:07.05"',
                        '"2015-10-02T'+(17+zoneOffsetHours)+':37:07.05"',
                        '"2015-10-02T'+(17+zoneOffsetHours)+':37:07.05"',
                        '"2015-10-02T'+(17+zoneOffsetHours)+':37:07.05"',
                        '"2015-10-02"'
                        ]
    }

    def "should deserialize LocalDateTime from legacy format"(){
      given:
      def noMillisDate = java.time.LocalDateTime.of(2015,10,02,17,37,07)

      expect:
      jsonConverter.fromJson('"2015-10-02T17:37:07"', java.time.LocalDateTime) == noMillisDate
    }
}
