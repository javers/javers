package org.javers.core.json.typeadapter

import org.javers.core.json.JsonConverter
import org.joda.time.LocalDateTime
import spock.lang.Specification

import static org.javers.core.json.JsonConverterBuilder.jsonConverter

/**
 * @author bartosz walacik
 */
class LocalDateTimeAdapterTest extends Specification {

    def "should convert LocalDateTime TO json in ISO format"() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        LocalDateTime date = new LocalDateTime(2001,12,1,22,23,03)
        String json = jsonConverter.toJson(date)

        then:
        json == '"2001-12-01T22:23:03"'
    }

    def "should convert LocalDateTime FROM json in ISO format"() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        LocalDateTime date = jsonConverter.fromJson('"2001-12-01T22:23:03"', LocalDateTime)

        then:
        date  == new LocalDateTime(2001,12,1,22,23,03)
    }

    class WithLocalDateTime {
        LocalDateTime ldt
    }

    def void "should be null safe when converting TO json"() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        String json = jsonConverter.toJson(new WithLocalDateTime())

        then:
        assert json.contains('"ldt": null')
    }

    def void "should be null safe when converting FROM json"() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        WithLocalDateTime value = jsonConverter.fromJson('{"ldt":null}', WithLocalDateTime)

        then:
        value.ldt == null
    }
}
