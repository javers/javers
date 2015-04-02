package org.javers.core.json.typeadapter.change

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.javers.core.diff.Change
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.json.JsonConverter
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.json.builder.ChangeTestBuilder.referenceChanged
import static org.javers.repository.jql.InstanceIdDTO.instanceId
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserDetailsBuilder.dummyUserDetails

/**
 * @author bartosz walacik
 */
class ReferenceChangeTypeAdapterTest extends Specification {

    def "should serialize ReferenceChange" () {
        given:
            JsonConverter jsonConverter = javersTestAssembly().jsonConverter
            ReferenceChange change = referenceChanged(dummyUser().build(),
                                                      "dummyUserDetails",
                                                      dummyUserDetails(1).build(),
                                                      dummyUserDetails(2).build())

        when:
            String jsonText = jsonConverter.toJson(change)
            //println(jsonText)

        then:
            def json = new JsonSlurper().parseText(jsonText)
            json.property == "dummyUserDetails"
            json.changeType == "ReferenceChange"
            json.globalId
            json.left.cdoId == 1
            json.left.entity == "org.javers.core.model.DummyUserDetails"
            json.right.cdoId == 2
            json.right.entity == "org.javers.core.model.DummyUserDetails"
    }

    def "should deserialize ReferenceChange"() {
        given:
            JsonConverter jsonConverter = javersTestAssembly().jsonConverter
            def json = new JsonBuilder()
            json
            {
                changeType "ReferenceChange"
                globalId {
                entity "org.javers.core.model.DummyUser"
                cdoId "kaz"
                }
                property "dummyUserDetails"
                left {
                entity "org.javers.core.model.DummyUserDetails"
                cdoId 1
                }
                right {
                entity "org.javers.core.model.DummyUserDetails"
                cdoId 2
                }
            }

        when:
            ReferenceChange change  = jsonConverter.fromJson(json.toString(),Change)

        then:
            change.affectedGlobalId  == instanceId("kaz",DummyUser)
            change.left  == instanceId(1,DummyUserDetails)
            change.right == instanceId(2,DummyUserDetails)
            change.propertyName  == "dummyUserDetails"
    }

    def "should be nullSafe when writing leftId & rightId for ReferenceChange" () {
        given:
        JsonConverter jsonConverter = javersTestAssembly().jsonConverter
        ReferenceChange change = referenceChanged(dummyUser().build(),
                                                  "dummyUserDetails",null, null)

        when:
        String jsonText = jsonConverter.toJson(change)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.rightReference == null
        json.leftReference == null
    }
}
