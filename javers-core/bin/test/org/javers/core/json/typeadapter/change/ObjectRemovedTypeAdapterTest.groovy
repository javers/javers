package org.javers.core.json.typeadapter.change

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.javers.core.diff.Change
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.json.JsonConverter
import org.javers.core.model.DummyUser
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.json.builder.ChangeTestBuilder.objectRemoved
import static org.javers.core.GlobalIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class ObjectRemovedTypeAdapterTest extends Specification {
    def "should serialize ObjectRemoved"() {
        given:
        JsonConverter jsonConverter = javersTestAssembly().jsonConverter
        def change = objectRemoved(new DummyUser(name:"kaz"))

        when:
        String jsonText = jsonConverter.toJson(change)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.changeType == "ObjectRemoved"
        json.globalId.entity == "org.javers.core.model.DummyUser"
        json.globalId.cdoId == "kaz"
    }

    def "should deserialize ObjectRemoved"() {
        given:
        JsonConverter jsonConverter = javersTestAssembly().jsonConverter
        def json = new JsonBuilder()
        json {
            changeType "ObjectRemoved"
            globalId {
                entity "org.javers.core.model.DummyUser"
                cdoId  "kaz"
            }
        }

        when:
        def change  = jsonConverter.fromJson(json.toString(),Change)

        then:
        change instanceof ObjectRemoved
        change.affectedGlobalId == instanceId("kaz",DummyUser)
    }
}
