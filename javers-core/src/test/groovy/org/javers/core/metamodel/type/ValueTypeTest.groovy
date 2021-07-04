package org.javers.core.metamodel.type

import org.javers.core.JaversBuilder
import org.javers.core.examples.model.Address
import spock.lang.Shared
import spock.lang.Specification
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

import static java.math.RoundingMode.HALF_UP

class ValueTypeTest extends Specification {
    @Shared
    def javers = JaversBuilder.javers().registerValue(Address).build()

    def "should calculate reflective toString" () {
        expect:
        javers.getTypeMapping(LocalDate) instanceof ValueType

        true
        // normal class
        calculateToString(new Address("a","b")) == "a,b"

        // WellKnownValueTypes class - delegated to standard toString()
        calculateToString(new BigDecimal(12.2).setScale(1, HALF_UP)) == "12.2"
        calculateToString(Locale.ENGLISH) == "en"

        // java core library class
        calculateToString(LocalDate.of(2019,02,13)) == "2019,2,13"
        calculateToString(LocalTime.of(1,2,3)) == "1,2,3,0"
        calculateToString(LocalDateTime.of(2019,02,01,1,2,3)) == "2019-02-01,01:02:03"
        calculateToString(Year.of(2019)) == "2019"
        calculateToString(ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]")) == "2007-12-03T10:15:30,+01:00,Europe/Paris"
        calculateToString(ZoneOffset.of("+05:00")) == "18000"
        calculateToString(OffsetDateTime.parse("2019-02-01T01:02:03+05:00")) == "2019-02-01T01:02:03,+05:00"
        calculateToString(Instant.parse("1980-04-09T10:15:30.00Z")) == "324123330,0"
        calculateToString(Period.between(LocalDate.of(2014, 1, 1), LocalDate.of(2015, 3, 7))) == "1,2,6"
        calculateToString(Duration.ofSeconds(6005)) == "6005,0"
        calculateToString(Duration.ofHours(3)) == "10800,0"
    }

    String calculateToString(def val) {
        javers.getTypeMapping(val.class).valueToString(val)
    }
}
