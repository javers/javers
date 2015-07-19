package org.javers.java8support

import spock.lang.Specification

import java.time.LocalDate

/**
 * @author bartosz.walacik
 */
class LocalDateTypeAdapterTest extends Specification {

    def "should convert LocalDate to and from ISO format"(){
        given:
        def adapter = new LocalDateTypeAdapter()

        expect:
        adapter.serialize( LocalDate.of(2001,01,31)) == "2001-01-31"
        adapter.deserialize("2001-01-31") == LocalDate.of(2001,01,31)

    }
}
