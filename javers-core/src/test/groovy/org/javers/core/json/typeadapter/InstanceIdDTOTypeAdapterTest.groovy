package org.javers.core.json.typeadapter

import groovy.json.JsonSlurper
import org.javers.core.model.DummyUser
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.repository.jql.InstanceIdDTO.instanceId

class InstanceIdDTOTypeAdapterTest extends Specification {

    def "should serialize InstanceIdDTO"() {
        given:
        def javers = javersTestAssembly()
        def id = instanceId("kaz", DummyUser)

        when:
        String jsonText = javers.jsonConverter.toJson(id)
        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.cdoId == "kaz"
        json.entity == "org.javers.core.model.DummyUser"
    }
}