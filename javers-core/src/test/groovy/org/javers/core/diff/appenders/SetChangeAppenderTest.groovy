package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.RealNodePair
import org.javers.core.model.DummyUser
import org.javers.core.model.SnapshotEntity
import org.joda.time.LocalDate
import spock.lang.Unroll

import static org.javers.core.diff.appenders.ContainerChangeAssert.getAssertThat
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author pawel szymczyk
 */
class SetChangeAppenderTest extends AbstractDiffAppendersTest {

    @Unroll
    def "should append #changesCount changes when left set is #leftSet and right set is #rightSet"() {

        when:
        def leftNode = buildGraph(dummyUser().withStringsSet(leftSet as Set).build())
        def rightNode = buildGraph(dummyUser().withStringsSet(rightSet as Set).build())

        def change = setChangeAppender().calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(DummyUser, "stringSet"))

        then:
        change.changes.size() == changesCount
        change.changes.each {
            assert it.index == null
        }

        where:
        leftSet              | rightSet             || changesCount
        null                 | ["1", "2"]           || 2
        []                   | ["1", "2"]           || 2
        ["1", "2"]           | ["1", "2", "3", "4"] || 2
        ["1", "2"]           | ["2", "1", "3"]      || 1
        ["1", "2"]           | []                   || 2
        ["1", "2", "3", "4"] | ["1"]                || 3
        ["2", "1", "3"]      | ["1", "2"]           || 1
    }

    @Unroll
    def "should not append changes when left set #leftSet and right set #rightSet are equal"() {

        when:
        def leftNode = buildGraph(dummyUser().withStringsSet(leftSet as Set).build())
        def rightNode = buildGraph(dummyUser().withStringsSet(rightSet as Set).build())

        def change = setChangeAppender().calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(DummyUser, "stringSet"))

        then:
        change == null

        where:
        leftSet        | rightSet
        []             | []
        ["1", "2"]     | ["1", "2"]
        ["1", "2"]     | ["2", "1"]
    }

    def "should append ValueAdded in Set of Values"() {
        given:
        def leftCdo =  new SnapshotEntity(setOfDates: [new LocalDate(2001,1,1)])
        def rightCdo = new SnapshotEntity(setOfDates: [new LocalDate(2001,5,5), new LocalDate(2001,1,1)])

        when:
        def change = setChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "setOfDates"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueAdded(new LocalDate(2001,5,5))

    }

    def "should append ValueRemoved in Set of Values"() {
        given:
        def leftCdo =  new SnapshotEntity(setOfDates: [new LocalDate(2001,5,5), new LocalDate(2001,1,1)])
        def rightCdo = new SnapshotEntity(setOfDates: [new LocalDate(2001,1,1)])

        when:
        def change = setChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "setOfDates"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueRemoved(new LocalDate(2001,5,5))

    }

}