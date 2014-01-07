package org.javers.core.json.typeadapter

import groovy.json.JsonSlurper
import org.javers.core.diff.Change
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.json.JsonConverter
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.json.JsonConverterBuilder.jsonConverter
import static org.javers.core.json.builder.ChangeTestBuilder.newObject
import static org.javers.core.json.builder.ChangeTestBuilder.objectRemoved
import static org.javers.core.json.builder.ChangeTestBuilder.valueChange
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

    def "should write property name, left & right values for ValueChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        ValueChange change = valueChange(dummyUser("kaz").build(),"flag",true,false)

        when:
        String jsonText = jsonConverter.toJson(change)
        System.out.println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.property == "flag"
        json.leftValue == true
        json.rightValue == false
    }

    @Unroll
    def "should write globalCdoId for #change.class.simpleName"(){
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        String jsonText = jsonConverter.toJson(change)

        expect:
        def json = new JsonSlurper().parseText(jsonText)
        json.changeType == expectedType
        json.globalCdoId.size() == 2
        json.globalCdoId.cdoId == "kaz"
        json.globalCdoId.entity == "org.javers.core.model.DummyUser"

        where:
        change << [newObject( dummyUser("kaz").build() ),
                   objectRemoved( dummyUser("kaz").build() ),
                   valueChange(dummyUser("kaz").build(),"flag",true,false)]
        expectedType << [NewObject.simpleName, ObjectRemoved.simpleName, ValueChange.simpleName]

    }

}
