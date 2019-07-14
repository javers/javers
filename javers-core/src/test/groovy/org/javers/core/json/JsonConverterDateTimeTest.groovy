package org.javers.core.json

import org.joda.time.DateTimeZone
import org.slf4j.LoggerFactory
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

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
    int zoneOffset = Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).getOffset().getTotalSeconds()/3600

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
    def "should convert joda.LocalDateTime from json #fromJson to json #expectedJson"(){
        when:
        def jodaTime =  jsonConverter.fromJson(fromJson, org.joda.time.LocalDateTime)

        then:
        jsonConverter.toJson(jodaTime) == expectedJson

        where:
        fromJson << [
                '"2015-10-02T17:37:07.050"',
                '"2015-10-02T17:37:07.05"',
                '"2015-10-02T17:37:07.0"',
                '"2015-10-02T17:37:07"'
        ]
        expectedJson << [
                '"2015-10-02T17:37:07.050"',
                '"2015-10-02T17:37:07.050"',
                '"2015-10-02T17:37:07.000"',
                '"2015-10-02T17:37:07.000"'
        ]
    }

    @Unroll
    def "should convert #expectedType to and from JSON (#expectedJson) in ISO format"() {
        given:
        println "ZonedDateTime.now:   " + ZonedDateTime.now()
        println "time:                " + Instant.ofEpochMilli(time)
        println "zone offset:         " + zoneOffset + "H"
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
                         org.joda.time.LocalDateTime,
                         org.joda.time.LocalDate
        ]
        givenValue <<   [new java.util.Date(time),
                         new java.sql.Timestamp(time),
                         new java.sql.Date(time),
                         new java.sql.Time(time),
                         new org.joda.time.LocalDateTime(time, DateTimeZone.UTC),
                         new org.joda.time.LocalDate(2015,10,02)
        ]
        expectedJson << [
                        '"2015-10-02T'+(17+zoneOffset)+':37:07.05"',
                        '"2015-10-02T'+(17+zoneOffset)+':37:07.05"',
                        '"2015-10-02T'+(17+zoneOffset)+':37:07.05"',
                        '"2015-10-02T'+(17+zoneOffset)+':37:07.05"',
                        '"2015-10-02T17:37:07.050"',
                        '"2015-10-02"'
                        ]
    }

    def "should deserialize org.joda.time.LocalDateTime from legacy format"(){
      given:
      def noMillisDate = new org.joda.time.LocalDateTime(2015,10,02,17,37,07)

      expect:
      jsonConverter.fromJson('"2015-10-02T17:37:07"', org.joda.time.LocalDateTime) == noMillisDate
    }
}
