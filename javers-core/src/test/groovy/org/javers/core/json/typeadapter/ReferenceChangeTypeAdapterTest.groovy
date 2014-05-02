package org.javers.core.json.typeadapter

import groovy.json.JsonSlurper
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.json.JsonConverter
import spock.lang.Specification

import static org.javers.core.json.JsonConverterBuilder.jsonConverter
import static org.javers.core.json.builder.ChangeTestBuilder.referenceChanged
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserDetailsBuilder.dummyUserDetails

/**
 * @author bartosz walacik
 */
class ReferenceChangeTypeAdapterTest extends Specification {

    def "should serialize ReferenceChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
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
        json.globalCdoId
        json.leftReference.cdoId == 1
        json.leftReference.entity == "org.javers.core.model.DummyUserDetails"
        json.rightReference.cdoId == 2
        json.rightReference.entity == "org.javers.core.model.DummyUserDetails"
    }

    def "should be nullSafe when writing leftId & rightId for ReferenceChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
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
