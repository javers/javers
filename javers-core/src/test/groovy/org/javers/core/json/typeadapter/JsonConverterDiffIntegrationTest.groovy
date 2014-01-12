package org.javers.core.json.typeadapter

import groovy.json.JsonSlurper
import org.javers.core.diff.Change
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.json.JsonConverter
import org.javers.core.model.DummyAddress
import org.joda.time.LocalDateTime
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.json.JsonConverterBuilder.jsonConverter
import static org.javers.core.json.builder.ChangeTestBuilder.*
import static org.javers.core.model.DummyUserWithValues.dummyUserWithDate
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserDetailsBuilder.dummyUserDetails

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

    def "should write property name, left & right values for ValueChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        ValueChange change = valueChange(dummyUser("kaz").build(),"flag",true,false)

        when:
        String jsonText = jsonConverter.toJson(change)
        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.property == "flag"
        json.leftValue == true
        json.rightValue == false
    }


    def "should write property name, leftId & rightId for ReferenceChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        ReferenceChange change = referenceChanged(dummyUser().build(),
                                                   "dummyUserDetails",
                                                   dummyUserDetails(1).build(),
                                                   dummyUserDetails(2).build())

        when:
        String jsonText = jsonConverter.toJson(change)
        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.property == "dummyUserDetails"
        json.leftReference.cdoId == 1
        json.leftReference.entity == "org.javers.core.model.DummyUserDetails"
        json.rightReference.cdoId == 2
        json.rightReference.entity == "org.javers.core.model.DummyUserDetails"
    }

    def "should be nullSafe when writing leftId & rightId for ReferenceChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        ReferenceChange change = referenceChanged(dummyUser().build(),
                "dummyUserDetails",null, null)

        when:
        String jsonText = jsonConverter.toJson(change)
        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.rightReference == null
        json.leftReference == null
    }

    def "should be nullSafe when writing left & right value for ValueChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        ValueChange change = valueChange(dummyUser("kaz").build(),"bigFlag",null, null)

        when:
        String jsonText = jsonConverter.toJson(change)
        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.leftValue == null
        json.rightValue == null

    }

    def "should use custom JsonTypeAdapter when writing Values like LocalDateTime for ValueChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        def dob = new LocalDateTime()
        ValueChange change = valueChange(dummyUserWithDate("kaz"),"dob",null, dob)

        when:
        String jsonText = jsonConverter.toJson(change)
        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.leftValue ==  null
        json.rightValue == LocalDateTimeTypeAdapter.ISO_FORMATTER.print(dob)
    }

    @Unroll
    def "should write globalCdoId for #change.class.simpleName"(){
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        String jsonText = jsonConverter.toJson(change)
        println(jsonText)

        expect:
        def json = new JsonSlurper().parseText(jsonText)
        json.changeType == expectedType
        json.instanceId.size() == 2
        json.instanceId.cdoId == "kaz"
        json.instanceId.entity == "org.javers.core.model.DummyUser"

        where:
        change << [newObject(dummyUser("kaz").build()),
                   objectRemoved(dummyUser("kaz").build()),
                   valueChange(dummyUser("kaz").build(),"flag",true,false),
                   referenceChanged(dummyUser("kaz").build(),"dummyUserDetails",dummyUserDetails(1).build(),null)]
        expectedType << [NewObject.simpleName,
                         ObjectRemoved.simpleName,
                         ValueChange.simpleName,
                         ReferenceChange.simpleName]

    }

    def "should write ValueObjectId & property for ValueObject property change"() {
        given:
        def jsonConverter = jsonConverter().build()
        def change = valueObjectPropertyChange(dummyUserDetails(1).build(),
                                                     DummyAddress,
                                                     "street",
                                                     "dummyAddress",
                                                     "Street 1", "Street 2");
        when:
        String jsonText = jsonConverter.toJson(change)
        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.property == "street"
        json.valueObjectId.size() == 3
        json.valueObjectId.fragment == "dummyAddress"
        json.valueObjectId.cdoId == "1"
        json.valueObjectId.getManagedClass == "org.javers.core.model.DummyUserDetails"

    }
}
