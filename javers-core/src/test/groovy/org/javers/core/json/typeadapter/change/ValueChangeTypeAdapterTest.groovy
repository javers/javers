package org.javers.core.json.typeadapter.change

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.javers.core.diff.Change
import org.javers.core.diff.changetype.ValueAddedChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.ValueRemovedChange
import org.javers.core.json.JsonConverter
import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters
import org.javers.core.model.DummyUser
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.json.builder.ChangeTestBuilder.valueChange
import static org.javers.core.GlobalIdTestBuilder.instanceId
import static org.javers.core.model.DummyUserWithValues.dummyUserWithDate

/**
 * @author bartosz walacik
 */
class ValueChangeTypeAdapterTest extends Specification {

    @Unroll
    def "should serialize #valueChangeType.simpleName" () {
        given:
        def jsonConverter = javersTestAssembly().jsonConverter
        def globalId = instanceId(new DummyUser(name:"kaz"))

        when:
        def json = new JsonSlurper().parseText( jsonConverter.toJson(change(globalId)) )

        then:
        println "valueChange JSON: " + json
        json.property == "flag"
        json.changeType == valueChangeType.simpleName
        json.globalId
        json.left == expectedLeft
        json.right == expectedRight

        where:
        valueChangeType  << [ValueChange, ValueAddedChange, ValueRemovedChange]
        change << [
                {id -> new ValueChange(id, "flag", true, false)},
                {id -> new ValueAddedChange(id, "flag", true)},
                {id -> new ValueRemovedChange(id, "flag", true)}
        ]
        expectedLeft <<  [true,  null, true]
        expectedRight << [false, true, null]
    }

    @Unroll
    def "should deserialize #valueChangeType.simpleName"() {
        given:
            JsonConverter jsonConverter = javersTestAssembly().jsonConverter
            def json = new JsonBuilder()
            json {
                property  "bigFlag"
                changeType valueChangeType.simpleName
                left expectedLeft
                right expectedRight
                globalId {
                    entity "org.javers.core.model.DummyUser"
                    cdoId  "kaz"
                }
            }

        when:
            ValueChange change  = jsonConverter.fromJson(json.toString(),Change)

        then:
            change.class == valueChangeType
            change.affectedGlobalId == instanceId("kaz",DummyUser)
            change.left == expectedLeft
            change.right == expectedRight
            change.propertyName == "bigFlag"

        where:
        valueChangeType  << [ValueChange, ValueAddedChange, ValueRemovedChange]
        expectedLeft <<  [true,  null, true]
        expectedRight << [false, true, null]
    }

    def "should serialize ValueChange with Values using custom TypeAdapter" () {
        given:
        JsonConverter jsonConverter = javersTestAssembly().jsonConverter
        def dob = LocalDateTime.now()
        ValueChange change = valueChange(dummyUserWithDate("kaz"),"dob",null, dob)

        when:
        String jsonText = jsonConverter.toJson(change)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.left ==  null
        json.right == UtilTypeCoreAdapters.serialize(dob)
    }

    def "should deserialize ValueChange with Values using custom TypeAdapter"() {
        given:
        JsonConverter jsonConverter = javersTestAssembly().jsonConverter
        def json = new JsonBuilder()
        json {
            property  "dob"
            changeType "ValueChange"
            left null
            right "2001-01-01"
            globalId {
                entity "org.javers.core.model.SnapshotEntity"
                cdoId  1
            }
        }

        when:
        ValueChange change  = jsonConverter.fromJson(json.toString(),Change)

        then:
        change.left == null
        change.right == new LocalDate(2001,1,1)
    }


    def "should be nullSafe when writing ValueChange" () {
        given:
        JsonConverter jsonConverter = javersTestAssembly().jsonConverter
        ValueChange change = valueChange(new DummyUser(name:"kaz"),"bigFlag",null, null)

        when:
        String jsonText = jsonConverter.toJson(change)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.left == null
        json.right == null
    }
}
