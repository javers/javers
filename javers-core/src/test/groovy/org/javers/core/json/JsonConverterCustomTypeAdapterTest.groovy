package org.javers.core.json

import org.joda.time.LocalDateTime
import spock.lang.Specification

import static org.javers.core.json.JsonConverterBuilder.jsonConverter

/**
 * @author bartosz walacik
 */
class JsonConverterCustomTypeAdapterTest extends Specification {


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

    def "should use custom type adapter when converting to json"() {
        given:
        JsonConverter jsonConverter =  JsonConverterBuilder.jsonConverter().
                                       registerJsonTypeAdapter(new DummyJsonPersonCustomTypeAdapter()).build()

        when:
        String json = jsonConverter.toJson( new DummyJsonPerson("mad","kaz"))

        then:
        json == '"mad@kaz"'
    }

    def  "should use custom type adapter when converting from json"() {
        given:
        JsonConverter jsonConverter =  JsonConverterBuilder.jsonConverter().
                                       registerJsonTypeAdapter(new DummyJsonPersonCustomTypeAdapter()).build()
        when:
        DummyJsonPerson person = jsonConverter.fromJson('"mad@kaz"',DummyJsonPerson.class)

        then:
        person == new DummyJsonPerson("mad","kaz")
    }

    def "should use custom native Gson type adapter when converting to json"() {
        given:
        JsonConverter jsonConverter =  JsonConverterBuilder.jsonConverter().
                                       registerNativeTypeAdapter(DummyJsonPerson, new DummyJsonPersonNativeTypeAdapter()).build()

        when:
        String json = jsonConverter.toJson( new DummyJsonPerson("mad","kaz"))

        then:
        json == '"mad@kaz"'
    }

    def  "should use custom native Gson type adapter when converting from json"() {
        given:
        JsonConverter jsonConverter =  JsonConverterBuilder.jsonConverter().
                                       registerNativeTypeAdapter(DummyJsonPerson, new DummyJsonPersonNativeTypeAdapter()).build()
        when:
        DummyJsonPerson person = jsonConverter.fromJson('"mad@kaz"',DummyJsonPerson.class)

        then:
        person == new DummyJsonPerson("mad","kaz")
    }

    def void "custom type adapters should be null safe when converting to json"() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        String json = jsonConverter.toJson(null, LocalDateTime)

        then:
        json == "null"
    }


    def void "custom type adapters should be null safe when converting from json"() {
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        LocalDateTime value = jsonConverter.fromJson("null", LocalDateTime)

        then:
        value == null
    }
}
