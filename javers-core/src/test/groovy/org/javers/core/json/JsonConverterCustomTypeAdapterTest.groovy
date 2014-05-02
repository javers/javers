package org.javers.core.json

import org.javers.core.model.DummyPoint
import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class JsonConverterCustomTypeAdapterTest extends Specification {

    def "should use custom typeAdapter when converting to json"() {
        given:
        JsonConverter jsonConverter =  JsonConverterBuilder.jsonConverter().
                                       registerJsonTypeAdapter(new DummyPointJsonTypeAdapter()).build()

        when:
        String json = jsonConverter.toJson( new DummyPoint(1,2))

        then:
        json == '"1,2"'
    }

    def  "should use custom typeAdapter when converting from json"() {
        given:
        JsonConverter jsonConverter =  JsonConverterBuilder.jsonConverter().
                                       registerJsonTypeAdapter(new DummyPointJsonTypeAdapter()).build()
        when:
        DummyPoint person = jsonConverter.fromJson('"1,2"',DummyPoint.class)

        then:
        person == new DummyPoint(1,2)
    }

    def "should use custom native Gson typeAdapter when converting to json"() {
        given:
        JsonConverter jsonConverter =  JsonConverterBuilder.jsonConverter().
                                       registerNativeTypeAdapter(DummyPoint, new DummyPointNativeTypeAdapter()).build()

        when:
        String json = jsonConverter.toJson( new DummyPoint(1,2))

        then:
        json == '"1,2"'
    }

    def  "should use custom native Gson typeAdapter when converting from json"() {
        given:
        JsonConverter jsonConverter =  JsonConverterBuilder.jsonConverter().
                                       registerNativeTypeAdapter(DummyPoint, new DummyPointNativeTypeAdapter()).build()
        when:
        DummyPoint person = jsonConverter.fromJson('"1,2"',DummyPoint.class)

        then:
        person == new DummyPoint(1,2)
    }
}
