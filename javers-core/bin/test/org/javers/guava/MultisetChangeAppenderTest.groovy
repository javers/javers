package org.javers.guava

import com.google.common.collect.HashMultiset
import com.google.common.collect.TreeMultiset
import org.javers.core.JaversTestBuilder
import org.javers.core.diff.RealNodePair
import org.javers.core.diff.appenders.AbstractDiffAppendersTest
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import spock.lang.Unroll

import static org.javers.core.GlobalIdTestBuilder.valueObjectId

/**
 * @author akrystian
 */
class MultisetChangeAppenderTest extends AbstractDiffAppendersTest {

    MultisetChangeAppender multisetChangeAppender() {
        new MultisetChangeAppender()
    }

    @Unroll
    def "should append changes when left multiset is #leftList and right set is #rightList (even if Multisets' impl differs)"() {

        when:
        def leftNode = buildGraph(new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(leftList)))
        def rightNode = buildGraph(new SnapshotEntity(multiSetOfPrimitives: TreeMultiset.create(rightList)))

        def change = multisetChangeAppender().calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(SnapshotEntity, "multiSetOfPrimitives"))

        then:
        new ArrayList(change.addedValues).sort() == added.sort()
        change.removedValues == removed
        change.changes.each {
            assert it.index == null
        }

        where:
        leftList             | rightList            || added           | removed
        []                   | ["2", "1", "2"]      || ["2", "1", "2"] | []
        ["1", "2"]           | ["1", "2", "3", "4"] || ["3", "4"]      | []
        ["1", "2"]           | ["2", "1", "3"]      || ["3"]           | []
        ["1", "2"]           | []                   || []              | ["1", "2"]
        ["1", "2", "3", "4"] | ["1"]                || []              | ["2", "3", "4"]
        ["2", "1", "3"]      | ["1", "2"]           || []              | ["3"]
    }

    @Unroll
    def "should not append changes when left set #leftList and right set #rightList are equal"() {

        when:
        def leftNode = buildGraph(new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(leftList)))
        def rightNode = buildGraph(new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(rightList)))

        def change = multisetChangeAppender().calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(SnapshotEntity, "multiSetOfPrimitives"))

        then:
        change == null

        where:
        leftList   | rightList
        []         | []
        ["1", "2"] | ["1", "2"]
        ["1", "2"] | ["2", "1"]
    }

    def "should append ValueAdded in Multiset of Values"() {
        given:
        def leftNode = buildGraph(new SnapshotEntity(multiSetValueObject: HashMultiset.create(leftList)))
        def rightNode = buildGraph(new SnapshotEntity(multiSetValueObject: HashMultiset.create(rightList)))

        when:
        def change = multisetChangeAppender()
                .calculateChanges(new RealNodePair(leftNode, rightNode), getProperty(SnapshotEntity, "multiSetValueObject"))

        then:
        change.valueAddedChanges.size() == 1
        change.changes[0].addedValue == valueObjectId(1, SnapshotEntity, "multiSetValueObject/4057abef011cfffbdbaa632f49dcef56")

        where:
        leftList <<  [[new DummyAddress(city: "New York"),
                       new DummyAddress(city: "New York")] ]
        rightList << [[new DummyAddress(city: "New York"),
                       new DummyAddress(city: "New York"),
                       new DummyAddress(city: "Buffalo")] ]
    }

    def "should append ValueRemoved in Multiset of Values"() {
        given:
        def leftNode = buildGraph(new SnapshotEntity(multiSetValueObject: HashMultiset.create(leftList)))
        def rightNode = buildGraph(new SnapshotEntity(multiSetValueObject: HashMultiset.create(rightList)))

        when:
        def change = multisetChangeAppender()
                .calculateChanges(new RealNodePair(leftNode, rightNode), getProperty(SnapshotEntity, "multiSetValueObject"))

        then:
        change.valueRemovedChanges.size() == 1
        change.changes[0].removedValue == valueObjectId(1, SnapshotEntity, "multiSetValueObject/4057abef011cfffbdbaa632f49dcef56")

        where:
        leftList <<  [[new DummyAddress(city: "New York"),
                       new DummyAddress(city: "New York"),
                       new DummyAddress(city: "Buffalo")] ]
        rightList << [[new DummyAddress(city: "New York"),
                       new DummyAddress(city: "New York")] ]
    }
}
