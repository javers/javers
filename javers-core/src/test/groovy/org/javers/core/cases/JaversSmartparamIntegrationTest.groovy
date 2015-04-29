package org.javers.core.cases

import groovy.json.JsonSlurper
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.Diff
import org.javers.core.metamodel.object.UnboundedValueObjectId
import org.javers.core.model.DummyParameterEntry
import org.javers.core.model.DummyUser
import org.joda.time.LocalDate
import spock.lang.Specification

import static org.javers.core.model.DummyUser.Sex.FEMALE

/**
 * @author bartosz walacik
 */
class JaversSmartparamIntegrationTest extends Specification{

    def "should serialize parameter entry"() {
        given:
        Javers javers = JaversBuilder.javers()
                                     .typeSafeValues()
                                     .build()

        def entry1 = new DummyParameterEntry(["date":new LocalDate(2014,01,10)])
        def entry2 = new DummyParameterEntry(["date":new LocalDate(2014,01,12),
                                              "rate":new BigDecimal(10),
                                              "int" :1,
                                              "String":"str",
                                              "enum":FEMALE])

        when:
        Diff diff = javers.compare(entry1, entry2)
        String jsonText = javers.toJson(diff)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        def mapChange = json.changes[0]

        mapChange.changeType == "MapChange"
        mapChange.globalId.valueObject == "org.javers.core.model.DummyParameterEntry"
        mapChange.property == "levels"
        mapChange.entryChanges.size() == 5
        List sortedEntryChanges = mapChange.entryChanges.sort{it.key}

        with(sortedEntryChanges[0]) {
            entryChangeType == "EntryAdded"
            key == "String"
            value == "str"
        }

        with(sortedEntryChanges[1]) {
            entryChangeType == "EntryValueChange"
            key == "date"
            leftValue.typeAlias == "LocalDate"
            leftValue.value == "2014-01-10"
            rightValue.value == "2014-01-12"
            rightValue.typeAlias == "LocalDate"
        }

        with(sortedEntryChanges[2]) {
            entryChangeType == "EntryAdded"
            key == "enum"
            value.typeAlias == "Sex"
            value.value == FEMALE.name()
        }

        with(sortedEntryChanges[3]) {
            entryChangeType == "EntryAdded"
            key == "int"
            value == 1
        }

        with(sortedEntryChanges[4]) {
            entryChangeType == "EntryAdded"
            key == "rate"
            value.typeAlias == "BigDecimal"
            value.value == 10
        }
    }
}
