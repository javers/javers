package org.javers.core.cases

import groovy.json.JsonSlurper
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.Diff
import org.javers.core.model.DummyParameterEntry
import spock.lang.Specification

import java.time.LocalDate

import static org.javers.core.model.DummyUser.Sex.FEMALE

/**
 * @author bartosz walacik
 */
class JaversSmartparamIntegrationTest extends Specification{

    def "should serialize parameter entry"() {
        given:
        Javers javers = JaversBuilder.javers()
                                     .withTypeSafeValues(true)
                                     .build()

        def entry1 = new DummyParameterEntry(["util": LocalDate.of(2014,01,10)])
        def entry2 = new DummyParameterEntry(["util": LocalDate.of(2014,01,12),
                                              "rate":new BigDecimal(10),
                                              "int" :1,
                                              "String":"str",
                                              "enum":FEMALE])

        when:
        Diff diff = javers.compare(entry1, entry2)
        String jsonText = javers.jsonConverter.toJson(diff)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        def mapChange = json.changes[0]

        mapChange.changeType == "MapChange"
        mapChange.globalId.valueObject == "org.javers.core.model.DummyParameterEntry"
        mapChange.property == "levels"
        mapChange.entryChanges.size() == 5

        with(mapChange.entryChanges.find{it -> it.key == "String"}) {
            entryChangeType == "EntryAdded"
            value == "str"
        }

        with(mapChange.entryChanges.find{it -> it.key == "util"}) {
            entryChangeType == "EntryValueChange"
            leftValue.typeAlias == "LocalDate"
            leftValue.value == "2014-01-10"
            rightValue.value == "2014-01-12"
            rightValue.typeAlias == "LocalDate"
        }

        with(mapChange.entryChanges.find{it -> it.key == "enum"}) {
            entryChangeType == "EntryAdded"
            value.typeAlias == "Sex"
            value.value == FEMALE.name()
        }

        with(mapChange.entryChanges.find{it -> it.key == "int"}) {
            entryChangeType == "EntryAdded"
            key == "int"
            value == 1
        }

        with(mapChange.entryChanges.find{it -> it.key == "rate"}) {
            entryChangeType == "EntryAdded"
            value.typeAlias == "BigDecimal"
            value.value == 10
        }
    }
}
