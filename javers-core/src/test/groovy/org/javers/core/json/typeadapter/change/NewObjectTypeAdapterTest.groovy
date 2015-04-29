package org.javers.core.json.typeadapter.change

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.javers.core.diff.Change
import org.javers.core.diff.changetype.NewObject
import org.javers.core.json.JsonConverter
import org.javers.core.model.DummyUser
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.json.builder.ChangeTestBuilder.newObject
import static org.javers.repository.jql.InstanceIdDTO.instanceId

/**
 * @author bartosz walacik
 */
class NewObjectTypeAdapterTest extends Specification {
    def "should serialize NewObject"() {
        given:
        JsonConverter jsonConverter = javersTestAssembly().jsonConverter
        def change = newObject(new DummyUser(name:"kaz"))

        when:
        String jsonText = jsonConverter.toJson(change)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.changeType == "NewObject"
        json.globalId.entity == "org.javers.core.model.DummyUser"
        json.globalId.cdoId == "kaz"
    }

    def "should deserialize NewObject"() {
        given:
        JsonConverter jsonConverter = javersTestAssembly().jsonConverter
        def json = new JsonBuilder()
        json {
            changeType "NewObject"
            globalId {
                entity "org.javers.core.model.DummyUser"
                cdoId  "kaz"
            }
        }

        when:
        def change  = jsonConverter.fromJson(json.toString(),Change)

        then:
        change instanceof NewObject
        change.affectedGlobalId == instanceId("kaz",DummyUser)
    }
}
