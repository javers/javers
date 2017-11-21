package org.javers.core.diff.appenders

import org.javers.core.model.DummyUser
import org.javers.core.model.SnapshotEntity
import spock.lang.Unroll

import java.time.LocalDate

import static org.javers.core.diff.appenders.ContainerChangeAssert.getAssertThat
import static org.javers.core.model.DummyUser.dummyUser

/**
 * @author Sergey Kobyshev
 */
class SetListChangeAppenderTest extends AbstractDiffAppendersTest {

    @Unroll
    def "should append #changesCount changes when left list is #leftList and right list is #rightList"() {

        when:
        def leftNode =  dummyUser().withIntegerList(leftList)
        def rightNode = dummyUser().withIntegerList(rightList)

        def change = setListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        change.changes.size() == changesCount
        change.changes.each {
            assert it.index == null
        }

        where:
        leftList             | rightList            || changesCount
        null                 | [1, 2]               || 2
        []                   | [1, 2]               || 2
        [1, 2]               | [1, 2, 3, 4]         || 2
        [1, 2]               | [2, 1, 3]            || 1
        [1, 2]               | []                   || 2
        [1, 2, 3, 4]         | [1]                  || 3
        [2, 1, 3]            | [1, 2]               || 1
    }

    @Unroll
    def "should not append changes when left list #leftList and right list #rightList are equal"() {

        when:
        def leftNode = dummyUser().withIntegerList(leftList)
        def rightNode = dummyUser().withIntegerList(rightList)

        def change = setListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        change == null

        where:
        leftList       | rightList
        []             | []
        [1, 2]         | [1, 2]
        [1, 2]         | [2, 1]
    }

    def "should append ValueAdded in List of Values"() {
        given:
        def leftCdo =  new SnapshotEntity(listOfDates: [new LocalDate(2001,1,1)])
        def rightCdo = new SnapshotEntity(listOfDates: [new LocalDate(2001,5,5), new LocalDate(2001,1,1)])

        when:
        def change = setListChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfDates"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueAdded(new LocalDate(2001,5,5))

    }

    def "should append ValueRemoved in List of Values"() {
        given:
        def leftCdo =  new SnapshotEntity(listOfDates: [new LocalDate(2001,5,5), new LocalDate(2001,1,1)])
        def rightCdo = new SnapshotEntity(listOfDates: [new LocalDate(2001,1,1)])

        when:
        def change = setListChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfDates"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueRemoved(new LocalDate(2001,5,5))

    }

}