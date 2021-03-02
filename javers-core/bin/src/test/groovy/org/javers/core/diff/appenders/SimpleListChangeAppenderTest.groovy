package org.javers.core.diff.appenders

import org.javers.core.model.DummyUser
import spock.lang.Unroll

import static org.javers.core.model.DummyUser.dummyUser

class SimpleListChangeAppenderTest extends AbstractDiffAppendersTest {

    def "should index List changes"() {
        given:
        def leftNode =  dummyUser().withIntegerList([])
        def rightNode = dummyUser().withIntegerList([1, 2])

        when:
        def change = listChangeAppender().calculateChanges(
                     realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        ContainerChangeAssert.assertThat(change).hasSize(2).hasIndexes([0,1])
    }

    @Unroll
    def "should append #changesCount changes when left list is #leftList and right list is #rightList"() {

        when:
        def leftNode =  dummyUser().withIntegerList(leftList)
        def rightNode = dummyUser().withIntegerList(rightList)

        def change = listChangeAppender().calculateChanges(
                     realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        change.changes.size() == changesCount

        where:
        leftList     | rightList    || changesCount
        []           | [1, 2]       || 2
        []           | [1, 2, 2, 2] || 4
        [1, 2]       | [1, 2, 3, 4] || 2
        [1, 2]       | [1, 2, 2, 2] || 2
        [1, 2]       | [2, 1]       || 2
        [1, 2]       | [2, 1, 2, 3] || 4
        [1, 2, 2, 2] | []           || 4
        [1, 2, 3, 4] | [1, 2]       || 2
        [1, 2, 2, 2] | [1, 2]       || 2
        [2, 1, 2, 3] | [1, 2]       || 4
    }

    @Unroll
    def "should not append changes when left list #leftList and right list #rightList are equal"() {

        when:
        def leftNode =  dummyUser().withIntegerList(leftList)
        def rightNode = dummyUser().withIntegerList(rightList)

        def change = listChangeAppender().calculateChanges(
                     realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        change == null

        where:
        leftList | rightList
        []       | []
        [1, 2]   | [1, 2]
    }
}
