package org.javers.guava.multiset
import com.google.common.collect.HashMultiset
import com.google.common.collect.TreeMultiset
import org.javers.core.diff.RealNodePair
import org.javers.core.diff.appenders.AbstractDiffAppendersTest
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import spock.lang.Unroll

import static org.javers.core.diff.appenders.ContainerChangeAssert.getAssertThat
import static org.javers.repository.jql.ValueObjectIdDTO.valueObjectId
/**
 * @author akrystian
 */
class MultisetChangeAppenderTest extends AbstractDiffAppendersTest {


    @Unroll
    def "should append #changesCount changes when left multiset is #leftList and right set is #rightList"() {

        when:
        def leftNode = buildGraph(new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(leftList)))
        def rightNode = buildGraph(new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(rightList)))

        def change = multisetChangeAppender().calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(SnapshotEntity, "multiSetOfPrimitives"))

        then:
        change.changes.size() == changesCount
        change.changes.each {
            assert it.index == null
        }

        where:
        leftList             | rightList            || changesCount
        []                   | ["1", "2"]           || 2
        ["1", "2"]           | ["1", "2", "3", "4"] || 2
        ["1", "2"]           | ["2", "1", "3"]      || 1
        ["1", "2"]           | []                   || 2
        ["1", "2", "3", "4"] | ["1"]                || 3
        ["2", "1", "3"]      | ["1", "2"]           || 1
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
        assertThat(change)
                .hasSize(1)
                .hasValueAdded(expectedVal)
        where:
        leftList <<  [[new DummyAddress(city: "New York"),
                       new DummyAddress(city: "New York")] ]
        rightList << [[new DummyAddress(city: "New York"),
                       new DummyAddress(city: "New York"),
                       new DummyAddress(city: "Buffalo")] ]
        expectedVal << [valueObjectId(1, SnapshotEntity, "multiSetValueObject/4057abef011cfffbdbaa632f49dcef56")]
    }

    def "should append ValueRemoved in Multiset of Values"() {
        given:
        def leftNode = buildGraph(new SnapshotEntity(multiSetValueObject: HashMultiset.create(leftList)))
        def rightNode = buildGraph(new SnapshotEntity(multiSetValueObject: HashMultiset.create(rightList)))

        when:
        def change = multisetChangeAppender()
                .calculateChanges(new RealNodePair(leftNode, rightNode), getProperty(SnapshotEntity, "multiSetValueObject"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueRemoved(expectedVal)
        where:
        leftList <<  [[new DummyAddress(city: "New York"),
                       new DummyAddress(city: "New York"),
                       new DummyAddress(city: "Buffalo")] ]
        rightList << [[new DummyAddress(city: "New York"),
                       new DummyAddress(city: "New York")] ]
        expectedVal << [valueObjectId(1, SnapshotEntity, "multiSetValueObject/4057abef011cfffbdbaa632f49dcef56")]
    }

    def "should append #changesCount changes for different multiset implementations"(){
        given:
        def leftNode = buildGraph(new SnapshotEntity(multiSetOfPrimitives: HashMultiset.create(leftList)))
        def rightNode = buildGraph(new SnapshotEntity(multiSetOfPrimitives: TreeMultiset.create(rightList)))

        when:
        def change = multisetChangeAppender().calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(SnapshotEntity, "multiSetOfPrimitives"))

        then:
        change.changes.size() == changesCount
        change.changes.each {
            assert it.index == null
        }

        where:
        leftList             | rightList            || changesCount
        []                   | ["1", "2"]           || 2
        ["1", "2"]           | ["1", "2", "3", "4"] || 2
        ["1", "2"]           | ["2", "1", "3"]      || 1
        ["1", "2"]           | []                   || 2
        ["1", "2", "3", "4"] | ["1"]                || 3
        ["2", "1", "3"]      | ["1", "2"]           || 1
    }

}
