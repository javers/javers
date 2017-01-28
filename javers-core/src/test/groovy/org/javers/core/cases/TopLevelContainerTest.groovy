package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.container.ArrayChange
import org.javers.core.diff.changetype.container.ListChange
import org.javers.core.diff.changetype.container.SetChange
import org.javers.core.diff.changetype.map.MapChange
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.repository.jql.UnboundedValueObjectIdDTO.*

/**
 * @author bartosz walacik
 */
class TopLevelContainerTest extends Specification {

    @Unroll
    def "should compare top-level #containerType"() {
        given:
        def javers = JaversBuilder.javers().build();

        when:
        def diff = javers.compare(container1, container2)

        then:
        diff.changes.size() == 1
        with(diff.changes[0]) {
            propertyName == pName
            changes.size() == 1
        }

        where:
        pName << ["map","list","set", "array", "array"]
        containerType << ["map","list","set", "primitive array", "object array"]
        expectedChangeType << [MapChange, ListChange, SetChange, ArrayChange, ArrayChange]
        container1 << [ [a:1], [1], [1] as Set, intArray([1,2]), ["a","b"].toArray()]
        container2 << [ [a:1, b:2], [1,2], [1,2] as Set, intArray([1,2,3]), ["a","b","c"].toArray()]
    }

    int[] intArray(List values){
        def ret = new int[values.size()]
        values.eachWithIndex{ def entry, int i -> ret[i] = entry}
        println ret
        ret
    }

    @Unroll
    def "should allow committing and querying top-level #colType(s)"() {
        given:
        def javers = JaversBuilder.javers().build();

        when:
        javers.commit("author",container1)
        javers.commit("author",container2)


        def changes = javers.findChanges(QueryBuilder.byGlobalId(voId).limit(3).build())

        then:
        changes[0].propertyName == colType

        where:
        colType << ["map","list","set", "array"]
        expectedChangeType << [MapChange, ListChange, SetChange, ArrayChange]
        container1 << [ [a:1], [1], [1] as Set, [1, 2].toArray()]
        container2 << [ [a:1 , b:2], [1,2], [1,2] as Set, [1].toArray()]
        voId << [unboundedMapId(), unboundedListId(), unboundedSetId(), unboundedArrayId()]
    }
}

