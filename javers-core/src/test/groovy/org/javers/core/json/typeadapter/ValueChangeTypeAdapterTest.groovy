package org.javers.core.json.typeadapter

import groovy.json.JsonSlurper
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.json.JsonConverter
import org.joda.time.LocalDateTime
import spock.lang.Specification

import static org.javers.core.json.JsonConverterBuilder.jsonConverter
import static org.javers.core.json.builder.ChangeTestBuilder.valueChange
import static org.javers.core.model.DummyUserWithValues.dummyUserWithDate
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class ValueChangeTypeAdapterTest extends Specification {

    def "should serialize ValueChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        ValueChange change = valueChange(dummyUser("kaz").build(),"flag",true,false)

        when:
        String jsonText = jsonConverter.toJson(change)
        // println jsonText

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.property == "flag"
        json.changeType == "ValueChange"
        json.globalCdoId
        json.leftValue == true
        json.rightValue == false
    }

    def "should be nullSafe when writing ValueChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        ValueChange change = valueChange(dummyUser("kaz").build(),"bigFlag",null, null)

        when:
        String jsonText = jsonConverter.toJson(change)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.leftValue == null
        json.rightValue == null
    }

    def "should use custom JsonTypeAdapter when writing Values like LocalDateTime for ValueChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        def dob = new LocalDateTime()
        ValueChange change = valueChange(dummyUserWithDate("kaz"),"dob",null, dob)

        when:
        String jsonText = jsonConverter.toJson(change)
        //println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.leftValue ==  null
        json.rightValue == LocalDateTimeTypeAdapter.ISO_FORMATTER.print(dob)
    }
}
