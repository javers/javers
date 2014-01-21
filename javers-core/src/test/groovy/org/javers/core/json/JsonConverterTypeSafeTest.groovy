package org.javers.core.json

import groovy.json.JsonSlurper
import org.javers.core.diff.changetype.map.EntryAdded
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.json.JsonConverterBuilder.jsonConverter

/**
 * @author bartosz walacik
 */
class JsonConverterTypeSafeTest extends Specification {
    def "should convert value to json type safely when switched on"() {
        given:
        JsonConverter jsonConverter = jsonConverter().typeSafeValues(true).build();

        when:
        def jsonText = jsonConverter.toJson(new EntryAdded(new LocalDate(2000, 1, 1), null));
        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.key.typeAlias == "LocalDate"
        json.key.value == "2000-01-01"
    }

    @Unroll
    def "should not wrap primitive #primitive.class.simpleName when typeSafe is switched on"() {
        given:
        JsonConverter jsonConverter = jsonConverter().typeSafeValues(true).build();

        expect:
        def jsonText = jsonConverter.toJson(new EntryAdded(primitive, null));
        //println(jsonText)
        def json = new JsonSlurper().parseText(jsonText)
        json.key == primitive

        where:
        primitive << ["String", true, 1, 1.1.doubleValue()]

    }

    def "should convert value to json without type safety by default"() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        def jsonText = jsonConverter.toJson(new EntryAdded(new LocalDate(2000, 1, 1), null));
      //  println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.key == "2000-01-01"
    }
}
