package org.javers.core.json.typeadapter

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.javers.core.metamodel.object.GlobalId
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.object.UnboundedValueObjectId
import org.javers.core.metamodel.object.ValueObjectId
import org.javers.repository.jql.ValueObjectIdDTO
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.repository.jql.InstanceIdDTO.instanceId

/**
 * @author bartosz walacik
 */
class GlobalIdTypeAdapterTest extends Specification {

    def class IdHolder{
        GlobalId id
    }

    @Unroll
    def "should deserialize InstanceId with #type cdoId"() {
        when:
        //println givenJson
        def idHolder = javersTestAssembly().jsonConverter.fromJson(givenJson, IdHolder)

        then:
        idHolder.id instanceof InstanceId
        idHolder.id == expectedId

        where:
        type << ["String", "Long"]
        givenJson << [
                '{"id":{"entity":"org.javers.core.model.DummyUser","cdoId":"kaz"}}',
                '{"id":{"entity":"org.javers.core.model.DummyUserDetails","cdoId":1}}'
                ]
        expectedId <<[
                instanceId("kaz", DummyUser),
                instanceId(1L, DummyUserDetails)
        ]
    }

    def "should serialize InstanceId"() {
        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().instanceId("kaz",DummyUser)

        when:
        String jsonText = javers.jsonConverter.toJson(id)
        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.cdoId == "kaz"
        json.entity == "org.javers.core.model.DummyUser"
    }

    def "should serialize UnboundedValueObjectId"() {
        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().unboundedValueObjectId(DummyAddress)

        when:
        String jsonText = javers.jsonConverter.toJson(id)
        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.valueObject == "org.javers.core.model.DummyAddress"
    }

    def "should deserialize UnboundedValueObjectId"() {
        given:
        def json = '{"id":{"valueObject":"org.javers.core.model.DummyAddress","cdoId":"/"}}'
        def javers = javersTestAssembly()

        when:
        def idHolder = javers.jsonConverter.fromJson(json, IdHolder)

        then:
        idHolder.id instanceof UnboundedValueObjectId
        idHolder.id == javers.idBuilder().unboundedValueObjectId(DummyAddress)
    }


    def "should serialize ValueObjectId"() {
        given:
        def javers = javersTestAssembly()
        def id = javers.idBuilder().withOwner("kaz",DummyUser).voId(DummyAddress,"somePath")

        when:
        String jsonText = javers.jsonConverter.toJson(id)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.ownerId.entity == "org.javers.core.model.DummyUser"
        json.ownerId.cdoId ==  "kaz"
        json.valueObject == "org.javers.core.model.DummyAddress"
        json.fragment == "somePath"
    }

    def "should deserialize ValueObjectId"() {
        given:
        def json = new JsonBuilder()
        json.id {
            fragment "somePath"
            valueObject "org.javers.core.model.DummyAddress"
            ownerId {
                entity "org.javers.core.model.DummyUser"
                cdoId "kaz"
            }
        }

        when:
        def idHolder = javersTestAssembly().jsonConverter.fromJson(json.toString(), IdHolder)

        then:
        idHolder.id instanceof ValueObjectId
        idHolder.id == ValueObjectIdDTO.valueObjectId("kaz",DummyUser,"somePath")
    }

}
