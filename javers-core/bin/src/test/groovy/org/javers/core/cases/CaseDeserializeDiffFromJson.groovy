package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.diff.Diff
import spock.lang.Specification

class CaseDeserializeDiffFromJson extends Specification {

    def "should deserialize from json"() {
        given:
        def javers = JaversBuilder.javers().build()
        def diff = javers.compare(new Item(1234), new Item(5678))
        def json = javers.getJsonConverter().toJson(diff)

        when:
        def javersDiff = javers.getJsonConverter().fromJson(json, Diff)

        then:
        println javersDiff.toString()
        javersDiff.toString() == diff.toString()
    }

    class Item {
        Integer id

        Item(Integer id) {
            this.id = id
        }
    }
}
