package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.RealNodePair
import org.javers.core.model.DummyUser
import spock.lang.Unroll

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author pawel szymczyk
 */
class ArrayChangeAppenderTest extends AbstractDiffTest {

    @Unroll
    def "should append #changesCount changes when left array is #leftArray and right array is #rightArray"() {

        when:
        def leftNode = buildGraph(dummyUser().withIntArray(leftArray as int[]).build())
        def rightNode = buildGraph(dummyUser().withIntArray(rightArray as int[]).build())

        def change = new ArrayChangeAppender(new MapChangeAppender(javers.typeMapper), javers.typeMapper)
                .calculateChanges(new RealNodePair(leftNode, rightNode), getProperty(DummyUser, "intArray"))

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
        def leftNode = buildGraph(dummyUser().withIntArray(leftArray as int[]).build())
        def rightNode = buildGraph(dummyUser().withIntArray(rightArray as int[]).build())

        def change = new ArrayChangeAppender(new MapChangeAppender(javers.typeMapper), javers.typeMapper)
                .calculateChanges(new RealNodePair(leftNode, rightNode), getProperty(DummyUser, "intArray"))

        then:
        change == null

        where:
        leftArray | rightArray
        []        | []
        [1, 2]    | [1, 2]
    }
}
