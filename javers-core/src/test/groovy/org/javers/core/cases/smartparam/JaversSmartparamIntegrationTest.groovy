package org.javers.core.cases.smartparam

import groovy.json.JsonSlurper
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.Diff
import org.javers.core.model.DummyParameterEntry
import org.joda.time.LocalDate
import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class JaversSmartparamIntegrationTest extends Specification{

    def "should serialize parameter entry"() {
        given:
        Javers javers = JaversBuilder.javers()
                                     .registerValueObject(DummyParameterEntry)
                                     .build()

        def entry1 = new DummyParameterEntry(["date":new LocalDate(2014,01,10)])
        def entry2 = new DummyParameterEntry(["date":new LocalDate(2014,01,12),"rate":new BigDecimal(10)])

        when:
        Diff diff = javers.compare("user", entry1, entry2)
        String jsonText = javers.toJson(diff)
        println("jsonText:\n"+jsonText)


        then:
        def json = new JsonSlurper().parseText(jsonText)
        def mapChange = json.changes[0]

        mapChange.changeType == "MapChange"
        mapChange.globalCdoId.valueObject == "org.javers.core.model.DummyParameterEntry"
        mapChange.globalCdoId.cdoId == "/"
        mapChange.property == "levels"
        mapChange.entryChanges.size() == 2
        mapChange.entryChanges[0].entryChangeType == "EntryValueChanged"
        mapChange.entryChanges[0].key == "date"
        mapChange.entryChanges[0].leftValue == "2014-01-10"
        mapChange.entryChanges[0].rightValue == "2014-01-12"
        mapChange.entryChanges[1].entryChangeType == "EntryAdded"
        mapChange.entryChanges[1].key == "rate"
        mapChange.entryChanges[1].value == 10

    }
}
