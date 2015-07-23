package org.javers.java8support

import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * @author bartosz.walacik
 */
class LocalDateTimeTypeAdapterTest extends Specification {

    def "should convert LocalDateTime to and from ISO format"(){
        given:
        def adapter = new LocalDateTimeTypeAdapter()
        def dateTime = LocalDateTime.of( LocalDate.of(2001,01,31), LocalTime.of(15,14,13) )

        expect:
        adapter.serialize( dateTime ) == "2001-01-31T15:14:13"
        adapter.deserialize("2001-01-31T15:14:13") == dateTime
    }
}
