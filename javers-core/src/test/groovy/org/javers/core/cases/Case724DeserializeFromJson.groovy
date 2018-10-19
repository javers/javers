package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.Diff
import spock.lang.Specification

class Case724DeserializeFromJson extends Specification {

    def "should deserialize from json"() {
        given:
            Item val1 = new Item(1234)
            Item val2 = new Item(5678)

            Javers javers = JaversBuilder.javers().build();
            Diff diff = javers.compare(val1, val2);
            String json = javers.getJsonConverter().toJson(diff);
        when:
            Diff javersDiff = javers.getJsonConverter().fromJson(json , Diff.class);
        then:
            javersDiff.toString() == diff.toString()
    }

    static class Item {
        Integer id

        Item(Integer id) {
            this.id = id
        }
    }
}
