package org.javers.core.diff.appenders

import org.javers.core.diff.RealNodePair
import org.javers.core.model.DummyUser
import org.javers.core.model.SnapshotEntity
import spock.lang.Shared
import spock.lang.Unroll

import java.time.LocalDate

import static org.javers.core.diff.appenders.ContainerChangeAssert.getAssertThat

/**
 * @author pawel szymczyk
 */
class SetChangeAppenderTest extends AbstractDiffAppendersTest {

    @Shared
    SetChangeAppender setChangeAppender

    @Shared
    String commonFieldName

    @Shared
    String dateFieldName

    def setupSpec() {
        setChangeAppender = setChangeAppender()
        commonFieldName = "stringSet"
        dateFieldName = "setOfDates"
    }

    @Unroll
    def "should append #changesCount changes when left field is #leftField and right field is #rightField"() {

        when:
        def leftNode = buildGraph(new DummyUser(name: 'name', "$commonFieldName": leftField))
        def rightNode = buildGraph(new DummyUser(name: 'name', "$commonFieldName": rightField))

        def change = setChangeAppender.calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(DummyUser, commonFieldName))

        then:
        change.changes.size() == changesCount
        change.changes.each {
            assert it.index == null
        }

        where:
        leftField            | rightField           || changesCount
        null                 | ["1", "2"]           || 2
        []                   | ["1", "2"]           || 2
        ["1", "2"]           | ["1", "2", "3", "4"] || 2
        ["1", "2"]           | ["2", "1", "3"]      || 1
        ["1", "2"]           | []                   || 2
        ["1", "2", "3", "4"] | ["1"]                || 3
        ["2", "1", "3"]      | ["1", "2"]           || 1
    }

    @Unroll
    def "should not append changes when left field #leftField and right field #rightField are equal"() {

        when:
        def leftNode = buildGraph(new DummyUser(name: 'name', "$commonFieldName": leftField))
        def rightNode = buildGraph(new DummyUser(name: 'name', "$commonFieldName": rightField))

        def change = setChangeAppender.calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(DummyUser, commonFieldName))

        then:
        change == null

        where:
        leftField  | rightField
        []         | []
        ["1", "2"] | ["1", "2"]
        ["1", "2"] | ["2", "1"]
    }

    def "should append ValueAdded in field of Values"() {
        given:
        def leftCdo = new SnapshotEntity("$dateFieldName": [new LocalDate(2001, 1, 1)])
        def rightCdo = new SnapshotEntity("$dateFieldName": [new LocalDate(2001, 5, 5), new LocalDate(2001, 1, 1)])

        when:
        def change = setChangeAppender
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, dateFieldName))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueAdded(new LocalDate(2001, 5, 5))

    }

    def "should append ValueRemoved in field of Values"() {
        given:
        def leftCdo = new SnapshotEntity("$dateFieldName": [new LocalDate(2001, 5, 5), new LocalDate(2001, 1, 1)])
        def rightCdo = new SnapshotEntity("$dateFieldName": [new LocalDate(2001, 1, 1)])

        when:
        def change = setChangeAppender
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, dateFieldName))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueRemoved(new LocalDate(2001, 5, 5))

    }

}