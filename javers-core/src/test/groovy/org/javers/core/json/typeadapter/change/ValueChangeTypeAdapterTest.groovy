package org.javers.core.json.typeadapter.change

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.javers.core.diff.Change
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.json.JsonConverter
import org.javers.core.json.typeadapter.joda.LocalDateTimeTypeAdapter
import org.javers.core.model.DummyUser
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.json.builder.ChangeTestBuilder.valueChange
import static org.javers.repository.jql.InstanceIdDTO.instanceId
import static org.javers.core.model.DummyUserWithValues.dummyUserWithDate
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class ValueChangeTypeAdapterTest extends Specification {

    def "should serialize ValueChange" () {
        given:
        JsonConverter jsonConverter = javersTestAssembly().jsonConverter
        ValueChange change = valueChange(dummyUser("kaz").build(),"flag",true,false)

        when:
        String jsonText = jsonConverter.toJson(change)
        //println jsonText

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.property == "flag"
        json.changeType == "ValueChange"
        json.globalId
        json.left == true
        json.right == false
    }

    def "should deserialize ValueChange"() {
        given:
            JsonConverter jsonConverter = javersTestAssembly().jsonConverter
            def json = new JsonBuilder()
            json {
                property  "bigFlag"
                changeType "ValueChange"
                left null
                right true
                globalId {
                    entity "org.javers.core.model.DummyUser"
                    cdoId  "kaz"
                }
            }

        when:
            ValueChange change  = jsonConverter.fromJson(json.toString(),Change)

        then:
            change.affectedGlobalId == instanceId("kaz",DummyUser)
            change.left == null
            change.right == true
            change.propertyName == "bigFlag"
    }

    def "should serialize ValueChange with Values using custom TypeAdapter" () {
        given:
        JsonConverter jsonConverter = javersTestAssembly().jsonConverter
        def dob = new LocalDateTime()
        ValueChange change = valueChange(dummyUserWithDate("kaz"),"dob",null, dob)

        when:
        String jsonText = jsonConverter.toJson(change)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.left ==  null
        json.right == LocalDateTimeTypeAdapter.ISO_FORMATTER.print(dob)
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
        ValueChange change = valueChange(dummyUser("kaz").build(),"bigFlag",null, null)

        when:
        String jsonText = jsonConverter.toJson(change)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.left == null
        json.right == null
    }
}
