package org.javers.core.json.typeadapter

import groovy.json.JsonSlurper
import org.javers.core.diff.changetype.map.EntryAdded
import org.javers.core.diff.changetype.map.EntryRemoved
import org.javers.core.diff.changetype.map.EntryValueChange
import org.javers.core.diff.changetype.map.MapChange
import org.javers.core.json.JsonConverter
import spock.lang.Specification

import static org.javers.core.json.JsonConverterBuilder.jsonConverter
import static org.javers.core.json.builder.ChangeTestBuilder.mapChange
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class MapChangeTypeAdapterTest extends Specification {
    def "should serialize MapChange" () {
        given:
        JsonConverter jsonConverter = jsonConverter().build()
        def entryChanges = [new EntryAdded("some",1),
                            new EntryRemoved("some",2),
                            new EntryValueChange("mod",3,4)]

        MapChange change = mapChange(dummyUser("kaz").build(),"valueMap",entryChanges)

        when:
        String jsonText = jsonConverter.toJson(change)
        println(jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.property == "valueMap"
        json.changeType == "MapChange"
        json.globalCdoId
        json.entryChanges.size() == 3
        with(json.entryChanges[0]){
            entryChangeType == "EntryAdded"
            key == "some"
            value == 1
        }
        with(json.entryChanges[1]){
            entryChangeType == "EntryRemoved"
            key == "some"
            value == 2
        }
        with(json.entryChanges[2]){
            entryChangeType == "EntryValueChange"
            key == "mod"
            leftValue == 3
            rightValue == 4
        }
    }
}
