package org.javers.guavasupport

import com.google.common.collect.HashMultimap
import com.google.common.collect.HashMultiset
import com.google.common.collect.Multimap
import org.javers.core.model.SnapshotEntity
import org.javers.guava.multimap.MultimapChange
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.JaversBuilder.javers

/**
 * @author akrystian
 */
class GuavaAddOnE2ETest extends Specification {

    @Unroll
    def "should detect value changes in Multiset "() {
        def javers = javers().build()

        def left = new SnapshotEntity(multiSetValueObject: HashMultiset.create(leftList))
        def right = new SnapshotEntity(multiSetValueObject: HashMultiset.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 1
        def changes = diff.changes[0].changes
        changes.size() == extpectedChanges

        where:
        leftList     | rightList                | extpectedChanges
        ["New York"] | ["Boston"]               | 2
        ["New York"] | ["New York", "New York"] | 1
        []           | ["New York"]             | 1
        ["New York"] | []                       | 1
    }

    @Unroll
    def "should not detect any value changes in Multiset "() {
        def javers = javers().build()

        def left = new SnapshotEntity(multiSetValueObject: HashMultiset.create(leftList))
        def right = new SnapshotEntity(multiSetValueObject: HashMultiset.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 0

        where:
        leftList               | rightList
        ["New York"]           | ["New York"]
        []                     | []
        ["New York", "Boston"] | ["Boston", "New York"]
    }

    @Unroll
    def "should detect value changes in Multimap "() {
        def javers = javers().build()

        def left = new SnapshotEntity(multiMapValueObject: HashMultimap.create(leftList))
        def right = new SnapshotEntity(multiMapValueObject: HashMultimap.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 1
        def changes = ((MultimapChange) diff.changes[0]).entryChanges
        changes.size() == extpectedChanges

        where:
        leftList << [createMultiMap(["New York"], ["New York City"]),
                     createMultiMap(["New York", "New York"], ["New York City", "Buffalo"]),
                     HashMultimap.create()]
        rightList << [createMultiMap(["New York"], ["Buffalo"]),
                      createMultiMap(["New York", "New York", "Alabama"], ["New York City", "Buffalo", "Akron"]),
                      createMultiMap(["New York"], ["Buffalo"])]
        extpectedChanges << [2, 1, 1]
    }

    @Unroll
    def "should not detect value changes in Multimap "() {
        def javers = javers().build()

        def left = new SnapshotEntity(multiMapValueObject: HashMultimap.create(leftList))
        def right = new SnapshotEntity(multiMapValueObject: HashMultimap.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 0

        where:
        leftList << [createMultiMap(["New York"], ["Buffalo"]),
                     createMultiMap(["New York", "New York"], ["Buffalo", "New York City"]),
                     HashMultimap.create()]
        rightList << [createMultiMap(["New York"], ["Buffalo"]),
                      createMultiMap(["New York", "New York"], ["Buffalo", "New York City"]),
                      HashMultimap.create()]

    }

    private Multimap createMultiMap(List<String> keys, List<String> values) {
        def hashMultimap = HashMultimap.create()
        for (int i = 0; i < keys.size(); i++) {
            hashMultimap.put(keys[i], values[i])
        }
        hashMultimap
    }
}
