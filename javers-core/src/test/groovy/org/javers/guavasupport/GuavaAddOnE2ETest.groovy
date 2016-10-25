package org.javers.guavasupport
import com.google.common.collect.HashMultimap
import com.google.common.collect.HashMultiset
import com.google.common.collect.Multimap
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import org.javers.guava.multimap.MultimapChange
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.JaversBuilder.javers
/**
 * @author akrystian
 */
class GuavaAddOnE2ETest extends Specification {
    private javers

    void setup() {
        javers = javers().build()
    }

    @Unroll
    def "should detect value changes in Multiset of primitives"() {
        given:
        def left = new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(leftList))
        def right = new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(rightList))

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
    def "should not detect any value changes in Multiset of primitives"() {
        given:
        def left = new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(leftList))
        def right = new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(rightList))

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
    def "should detect value changes in Multiset of ValueObjects"() {
        given:
        def left = new SnapshotEntity(multiSetValueObject: HashMultiset.create(leftList))
        def right = new SnapshotEntity(multiSetValueObject: HashMultiset.create(rightList))

        when:
        def diff = javers.compare(left, right)
        
        then:
        diff.changes.size() == 1
        def changes = diff.changes
       changes.size() == extpectedChanges

        where:
        leftList                             | rightList                                                                | extpectedChanges
        [new DummyAddress(city: "New York")] | [new DummyAddress(city: "Buffalo")]                                      | 2
        [new DummyAddress(city: "New York")] | [new DummyAddress(city: "New York"), new DummyAddress(city: "New York")] | 1
        []                                   | [new DummyAddress(city: "New York")]                                     | 1
        [new DummyAddress(city: "New York")] | []                                                                       | 1
    }

    @Unroll
    def "should not detect any value changes in Multiset of ValueObjects"() {
        given:
        def left = new SnapshotEntity(multiSetValueObject: HashMultiset.create(leftList))
        def right = new SnapshotEntity(multiSetValueObject: HashMultiset.create(rightList))

        when:
        def diff = javers.compare(left, right)


        def size = diff.changes.size()
        then:
        size == 0

        where:
        leftList                                                                | rightList
        [new DummyAddress(city: "New York")]                                    | [new DummyAddress(city: "New York")]
        []                                                                      | []
        [new DummyAddress(city: "New York"), new DummyAddress(city: "Buffalo")] | [new DummyAddress(city: "New York"), new DummyAddress(city: "Buffalo")]
    }


    @Unroll 
    def "should detect value changes in Multimap of primitives "() {
        given:
        def left = new SnapshotEntity(multiMapOfPrimitives: HashMultimap.create(leftList))
        def right = new SnapshotEntity(multiMapOfPrimitives: HashMultimap.create(rightList))

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
    def "should detect value changes in Multimap of ValueObjects "() {
        given:
        def left = new SnapshotEntity(multiMapValueObject: HashMultimap.create(leftList))
        def right = new SnapshotEntity(multiMapValueObject: HashMultimap.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 1
        def changes = ((MultimapChange) diff.changes[0]).entryChanges
        changes.size() == extpectedChanges

        where:
        leftList << [createMultiMap(["New York"], [new DummyAddress(city: "New York", street: "Maple St")]),
                     createMultiMap(["New York", "New York"], [new DummyAddress(city: "New York", street: "Maple St"), new DummyAddress(city: "Buffalo", street: "Maple St")]),
                     HashMultimap.create()]
        rightList << [createMultiMap(["New York"], [new DummyAddress(city: "New York", street: "Troy Ave")]),
                      createMultiMap(["New York", "New York", "Alabama"], [new DummyAddress(city: "New York", street: "Maple St"), new DummyAddress(city: "Buffalo", street: "Maple St"), new DummyAddress(city: "Akron", street: "S Main St")]),
                      createMultiMap(["New York"], [new DummyAddress(city: "New York", street: "Maple St")])]
        extpectedChanges << [2, 1, 1]
    }

    @Unroll 
    def "should not detect value changes in Multimap of primitives"() {
        given:
        def left = new SnapshotEntity(multiMapOfPrimitives: HashMultimap.create(leftList))
        def right = new SnapshotEntity(multiMapOfPrimitives: HashMultimap.create(rightList))

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

    @Unroll
    def "should not detect value changes in Multimap of ValueObjects"() {
        given:
        def left = new SnapshotEntity(multiMapValueObject: HashMultimap.create(leftList))
        def right = new SnapshotEntity(multiMapValueObject: HashMultimap.create(rightList))

        when:
        def diff = this.javers.compare(left, right)

        then:
        diff.changes.size() == 0

        where:
        leftList << [createMultiMap(
                             ["New York"],
                             [new DummyAddress(city: "New York", street: "Maple St")]),
                     createMultiMap(
                             ["New York", "New York"],
                             [new DummyAddress(city: "New York", street: "Maple St"), new DummyAddress(city: "Buffalo", street: "Maple St")]),
                     HashMultimap.create()]
        rightList << [createMultiMap(
                             ["New York"],
                             [new DummyAddress(city: "New York", street: "Maple St")]),
                     createMultiMap(
                             ["New York", "New York"],
                             [new DummyAddress(city: "New York", street: "Maple St"), new DummyAddress(city: "Buffalo", street: "Maple St")]),
                     HashMultimap.create()]
    }


    @Unroll
    def "should detect value changes in map of ValueObjects "() {
        def javers = org.javers.core.JaversBuilder.javers().build()
        given:
        def left = new SnapshotEntity(mapPrimitiveToVO: leftMap)
        def right = new SnapshotEntity(mapPrimitiveToVO: rightMap)

        when:
        def diff = javers.compare(left, right)

        then:
        println diff
        diff.changes.size() == extpectedChanges

        where:
        leftMap << [["New York" : new DummyAddress(city: "New York", street: "Maple St")],
                    ["New York" : new DummyAddress(city: "New York", street: "Maple St"),"Alabama": new DummyAddress(city: "Buffalo", street: "Maple St")],
                    [:]]
        rightMap << [["New York":new DummyAddress(city: "New York", street: "Troy Ave")],
                     ["New York":new DummyAddress(city: "New York", street: "Maple St"),"Toronto":  new DummyAddress(city: "Buffalo", street: "Maple St")],
                     ["New York":new DummyAddress(city: "New York", street: "Maple St")]]
        extpectedChanges << [2, 1, 1]
    }

    private <K, V> Multimap createMultiMap(List<K> keys, List<V> values) {
        def hashMultimap = HashMultimap.create()
        for (int i = 0; i < keys.size(); i++) {
            hashMultimap.put(keys[i], values[i])
        }
        hashMultimap
    }
}
