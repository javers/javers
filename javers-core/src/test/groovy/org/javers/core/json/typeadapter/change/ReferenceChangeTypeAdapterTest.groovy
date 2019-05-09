package org.javers.core.json.typeadapter.change

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.javers.common.reflection.ReflectionUtil
import org.javers.core.diff.Change
import org.javers.core.diff.changetype.ReferenceAddedChange
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ReferenceRemovedChange
import org.javers.core.diff.changetype.ValueAddedChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.ValueRemovedChange
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.json.builder.ChangeTestBuilder.referenceAdded
import static org.javers.core.json.builder.ChangeTestBuilder.referenceChanged
import static org.javers.core.json.builder.ChangeTestBuilder.referenceRemoved
import static org.javers.core.model.DummyUser.dummyUser
import static org.javers.core.model.DummyUserDetails.dummyUserDetails
import static org.javers.core.GlobalIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class ReferenceChangeTypeAdapterTest extends Specification {

    @Unroll
    def "should serialize #change.class.simpleName" () {
        given:
        def jsonConverter = javersTestAssembly().jsonConverter

        when:
        def jsonText = jsonConverter.toJson(change)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.property == "dummyUserDetails"
        json.changeType == change.class.simpleName
        json.globalId
        json.left == expectedLeft
        json.right == expectedRight

        where:
        change << [
            referenceChanged(dummyUser(), "dummyUserDetails", dummyUserDetails(1), dummyUserDetails(2)),
            referenceAdded(dummyUser(), "dummyUserDetails", dummyUserDetails(3)),
            referenceRemoved(dummyUser(), "dummyUserDetails", dummyUserDetails(4))
        ]
        expectedLeft << [
            [ cdoId: 1, entity: "org.javers.core.model.DummyUserDetails"],
            null,
            [ cdoId: 4, entity: "org.javers.core.model.DummyUserDetails"]
        ]
        expectedRight << [
            [ cdoId: 2, entity: "org.javers.core.model.DummyUserDetails"],
            [ cdoId: 3, entity: "org.javers.core.model.DummyUserDetails"],
            null
        ]
    }

    @Unroll
    def "should deserialize ReferenceChange"() {
        given:
        def jsonConverter = javersTestAssembly().jsonConverter
        def jsonAsMap = [
            changeType: changeType.simpleName,
            globalId: [
                entity: "org.javers.core.model.DummyUser",
                cdoId: "kaz"
            ],
            property: "dummyUserDetails",
            left: expectedLeft,
            right: expectedRight
        ]

        when:
        def change  = jsonConverter.fromJson(jsonConverter.toJson(jsonAsMap),Change)

        then:
        change.class == changeType
        change.affectedGlobalId  == instanceId("kaz",DummyUser)
        change.left  == instanceIdFromJson(expectedLeft)
        change.right == instanceIdFromJson(expectedRight)
        change.propertyName  == "dummyUserDetails"

        where:
        changeType  << [ReferenceChange, ReferenceAddedChange, ReferenceRemovedChange]
        expectedLeft << [
                [ cdoId: 1, entity: "org.javers.core.model.DummyUserDetails"],
                null,
                [ cdoId: 4, entity: "org.javers.core.model.DummyUserDetails"]
        ]
        expectedRight << [
                [ cdoId: 2, entity: "org.javers.core.model.DummyUserDetails"],
                [ cdoId: 3, entity: "org.javers.core.model.DummyUserDetails"],
                null
        ]
    }

    private InstanceId instanceIdFromJson(Map json) {
        if (json == null) {
            return null
        }
        return instanceId(json.cdoId, ReflectionUtil.forName(json.entity))
    }

    def "should be nullSafe when writing leftId & rightId for ReferenceChange" () {
        given:
        def jsonConverter = javersTestAssembly().jsonConverter
        def change = referenceChanged(dummyUser(),"dummyUserDetails",null, null)

        when:
        def jsonText = jsonConverter.toJson(change)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.rightReference == null
        json.leftReference == null
    }
}
