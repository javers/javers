package org.javers.core.json.typeadapter

import groovy.json.JsonSlurper
import org.javers.core.JaversTestBuilder
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class GlobalCdoIdTypeAdapterTest extends Specification {

    def "should serialize InstanceId"() {
        given:
        def javers = JaversTestBuilder.javersTestAssembly();
        def id = javers.idBuilder().instanceId("kaz",DummyUser)

        when:
        String jsonText = javers.jsonConverter.toJson(id)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.cdoId == "kaz"
        json.entity == "org.javers.core.model.DummyUser"
    }

    def "should serialize UnboundedValueObjectId"() {
        given:
        def javers = JaversTestBuilder.javersTestAssembly();
        def id = javers.idBuilder().unboundedValueObjectId(DummyAddress)

        when:
        String jsonText = javers.jsonConverter.toJson(id)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.cdoId == "/"
        json.valueObject == "org.javers.core.model.DummyAddress"
    }

    def "should serialize ValueObjectId"() {
        given:
        def javers = JaversTestBuilder.javersTestAssembly();
        def id = javers.idBuilder().withOwner("kaz",DummyUser).voId(DummyAddress,"somePath")

        when:
        String jsonText = javers.jsonConverter.toJson(id)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.ownerId.entity == "org.javers.core.model.DummyUser"
        json.ownerId.cdoId ==  "kaz"
        json.valueObject == "org.javers.core.model.DummyAddress"
        json.fragment == "somePath"
    }

}
