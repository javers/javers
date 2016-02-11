package org.javers.core.diff.appenders

import org.javers.core.model.DummyUser
import spock.lang.Unroll

import static org.javers.core.model.DummyUser.dummyUser

/**
 * @author pawel szymczyk
 */
class ArrayChangeAppenderTest extends AbstractDiffAppendersTest {

    def "should index Array changes"() {
        given:
        def leftNode =  dummyUser().withIntArray([])
        def rightNode = dummyUser().withIntArray([1, 2])

        when:
        def change = arrayChangeAppender().calculateChanges(
                     realNodePair(leftNode, rightNode), getProperty(DummyUser, "intArray"))

        then:
        ContainerChangeAssert.assertThat(change).hasSize(2).hasIndexes([0,1])
    }

    @Unroll
    def "should append #changesCount changes when left array is #leftArray and right array is #rightArray"() {

        when:
        def leftNode = dummyUser().withIntArray(leftArray)
        def rightNode = dummyUser().withIntArray(rightArray)

        def change = arrayChangeAppender().calculateChanges(
                     realNodePair(leftNode, rightNode), getProperty(DummyUser, "intArray"))

        then:
        change.changes.size() == changesCount

        where:
        leftArray    | rightArray   || changesCount
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
    def "should not append changes when left array #leftArray and right array #rightArray is equal"() {

        when:
        def leftNode = dummyUser().withIntArray(leftArray)
        def rightNode = dummyUser().withIntArray(rightArray)

        def change = arrayChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "intArray"))

        then:
        change == null

        where:
        leftArray | rightArray
        []        | []
        [1, 2]    | [1, 2]
    }
}
