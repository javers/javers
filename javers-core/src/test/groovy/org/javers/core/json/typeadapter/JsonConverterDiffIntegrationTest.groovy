package org.javers.core.json.typeadapter

import org.javers.core.diff.Change
import org.javers.core.json.JsonConverter
import spock.lang.Specification

import static org.javers.core.json.JsonConverterBuilder.jsonConverter

/**
 * @author bartosz walacik
 */
class JsonConverterDiffIntegrationTest extends Specification {
    class ClassWithChange{
        Change change
    }

    def "should be null safe when converting to json"(){
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        String json = jsonConverter.toJson(new ClassWithChange())

        then:
        assert json.contains('"change": null')
    }

}
