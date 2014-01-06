package org.javers.core.json

import org.joda.time.LocalDateTime
import spock.lang.Specification

import static org.javers.core.json.JsonConverterBuilder.jsonConverter

/**
 * @author bartosz walacik
 */
class JsonConverterCustomTypeAdapterTest extends Specification {

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
}
