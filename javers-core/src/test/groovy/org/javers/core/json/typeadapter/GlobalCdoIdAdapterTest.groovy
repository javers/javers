package org.javers.core.json.typeadapter

import groovy.json.JsonSlurper
import org.javers.core.json.JsonConverter
import org.javers.core.json.JsonConverterBuilder
import org.javers.core.json.builder.GlobalCdoIdTestBuilder
import org.javers.core.metamodel.object.GlobalIdFactory
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.object.UnboundedValueObjectId
import org.javers.core.metamodel.object.ValueObjectId
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyPoint
import org.javers.core.model.DummyUser
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

class GlobalCdoIdAdapterTest extends Specification {

    @Shared
    JsonConverter jsonConverter

    def setupSpec() {
        GlobalIdFactory globalIdFactory = Stub()
        jsonConverter = JsonConverterBuilder.jsonConverter(globalIdFactory).build()
    }


    def "should serialize InstanceId to json"() {

        given:
        InstanceId instanceId = GlobalCdoIdTestBuilder.instanceId("Kazik", DummyUser)

        when:
        def json = new JsonSlurper().parseText(jsonConverter.toJson(instanceId))

        then:
        json.cdoId == "Kazik"
        json.entity == "org.javers.core.model.DummyUser"
    }

    def "should deserialize InstanceId from Json"() {

        given:
        def instanceIdAsString = /{"entity": "org.javers.core.model.DummyUser", "cdoId": "Kazik"}/

        when:
        InstanceId instanceId = jsonConverter.fromJson(instanceIdAsString, InstanceId)

        then:
        instanceId
    }

    def "should serialize ValueObjectId to json"() {

        given:
        InstanceId owner = GlobalCdoIdTestBuilder.instanceId("Kazik", DummyUser)
        ValueObjectId valueObjectId = GlobalCdoIdTestBuilder.valueObjectId(owner, DummyAddress, "fragment")

        when:
        println(jsonConverter.toJson(valueObjectId))
        def json = new JsonSlurper().parseText(jsonConverter.toJson(valueObjectId))

        then:
        json.ownerId.cdoId == "Kazik"
        json.ownerId.entity == "org.javers.core.model.DummyUser"
        json.fragment == "fragment"
    }

    def "should deserialize ValueObjectId from Json"() {

        given:
        def instanceIdAsString = /{"entity": "org.javers.core.model.DummyUser", "cdoId": "Kazik"}/

        when:
        InstanceId instanceId = jsonConverter.fromJson(instanceIdAsString, InstanceId)

        then:
        instanceId
    }

    @Ignore
    def "should serialize UnboundedValueObjectId to json"() {

        given:
        UnboundedValueObjectId instanceId = GlobalCdoIdTestBuilder.unboundedValueObjectId(DummyPoint)

        when:
        def json = jsonConverter.toJson(instanceId)

        then:
        json
    }
}

