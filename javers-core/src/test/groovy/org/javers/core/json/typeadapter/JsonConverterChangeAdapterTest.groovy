package org.javers.core.json.typeadapter

import groovy.json.JsonSlurper
import org.javers.core.diff.Change
import org.javers.core.diff.changetype.NewObject
import org.javers.core.json.JsonConverter
import org.javers.core.json.builder.ChangeTestBuilder
import spock.lang.Specification

import static org.javers.core.json.JsonConverterBuilder.jsonConverter
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class JsonConverterChangeAdapterTest extends Specification {
    class ClassWithChange{
        Change change
    }

    def "should be null safe when converting to json"(){
        given:
        JsonConverter jsonConverter = jsonConverter().build()

        when:
        Change change = null;
        String json = jsonConverter.toJson(new ClassWithChange())

        then:
        assert json.contains('"change": null')
    }

    def "should write changeType, cdoId & entity for NewObject"(){
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        NewObject change = ChangeTestBuilder.newObject( dummyUser("kaz").build() )

        when:
        String jsonText = jsonConverter.toJson(change)

        then:
        System.out.println(jsonText)
        def json = new JsonSlurper().parseText(jsonText)
        json.size() == 2
        json.changeType == "NewObject"
        json.globalCdoId.size() == 2
        json.globalCdoId.cdoId == "kaz"
        json.globalCdoId.entity == "org.javers.core.model.DummyUser"
    }

}
