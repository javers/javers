package org.javers.core.diff.appenders

import com.google.common.collect.HashMultiset
import com.google.common.collect.Multiset
import org.javers.core.diff.appenders.levenshtein.LevenshteinListChangeAppender
import org.javers.core.diff.changetype.container.ArrayChange
import org.javers.core.diff.changetype.container.ListChange
import org.javers.core.diff.changetype.container.SetChange
import org.javers.core.diff.changetype.map.MapChange
import org.javers.core.model.DummyUser
import org.javers.core.model.SnapshotEntity
import org.javers.guava.MultisetChange
import org.javers.guava.MultisetChangeAppender
import spock.lang.Unroll

import static org.javers.core.model.DummyUser.dummyUser

class LeftAndRightContainerTest extends AbstractDiffAppendersTest {

    def "should set left and right Multiset in MultisetChange"(){
        given:
        def left = new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(["a", "b"]))
        def right = new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(["c","b"]))

        when:
        def change = new MultisetChangeAppender()
                .calculateChanges(realNodePair(left,right), getProperty(SnapshotEntity, "multiSetOfPrimitives"))

        then:
        change instanceof MultisetChange
        change.left instanceof Multiset
        change.right instanceof Multiset
        change.left ==  HashMultiset.create(["a", "b"])
        change.right == HashMultiset.create(["c","b"])
    }

    def "should store left and right Array in ArrayChange"(){
        given:
        def left =  new SnapshotEntity(id:1, arrayOfIntegers:[1, 2])
        def right =  new SnapshotEntity(id:1, arrayOfIntegers:[1, 3])

        when:
        def change = arrayChangeAppender()
                .calculateChanges(realNodePair(left,right), getProperty(SnapshotEntity, "arrayOfIntegers"))

        then:
        change instanceof ArrayChange
        change.left instanceof Integer[]
        change.right instanceof Integer[]
        change.left ==  [1, 2]
        change.right == [1, 3]
    }

    def "should store left and right Set of primitives in SetChange"(){
        given:
        def left =  new SnapshotEntity(id:1, setOfIntegers:[1, 2] as Set)
        def right =  new SnapshotEntity(id:1, setOfIntegers:[1, 3] as Set)

        when:
        def change = new SetChangeAppender()
                .calculateChanges(realNodePair(left,right), getProperty(SnapshotEntity, "setOfIntegers"))

        then:
        change instanceof SetChange
        change.left instanceof Set
        change.right instanceof Set
        change.left ==  [1, 2] as Set
        change.right == [1, 3] as Set
    }

    def "should store left and right Map in MapChange"(){
        given:
        def left =  new SnapshotEntity(id:1, mapOfPrimitives:["a":1, "b":2])
        def right =  new SnapshotEntity(id:1, mapOfPrimitives:["a":1, "b":3])

        when:
        def change = new MapChangeAppender()
                .calculateChanges(realNodePair(left,right), getProperty(SnapshotEntity, "mapOfPrimitives"))

        then:
        change instanceof MapChange
        change.left instanceof Map
        change.right instanceof Map
        change.left ==  ["a":1, "b":2]
        change.right == ["a":1, "b":3]
    }

    @Unroll
    def "should store left and right List in ListChange when created by #appender.class.simpleName"(){
        when:
        def left = dummyUser().withIntegerList([0, 4])
        def right = dummyUser().withIntegerList([0, 6])

        def change = appender.calculateChanges(
                realNodePair(left, right), getProperty(DummyUser, "integerList"))

        then:
        change instanceof ListChange
        change.left instanceof List
        change.left instanceof List
        change.left == [0, 4]
        change.right == [0, 6]

        where:
        appender << [
                new LevenshteinListChangeAppender(),
                collectionAsListChangeAppender(),
                listAsSetChangeAppender(),
                listChangeAppender()
        ]
    }
}
