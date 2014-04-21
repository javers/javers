package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.RealNodePair
import org.javers.core.model.DummyUser
import spock.lang.Unroll

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.test.builder.DummyUserBuilder.dummyUser


class ListChangeAppenderTest extends AbstractDiffTest {

    @Unroll
    def "should append #changesCount changes when left list is #leftList and right list is #rightList"() {

        when:
        def leftNode = buildGraph(dummyUser().withIntegerList(leftList as List).build())
        def rightNode = buildGraph(dummyUser().withIntegerList(rightList as List).build())

        def change = new ListChangeAppender(new MapChangeAppender(javers.typeMapper), javers.typeMapper).calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        change.changes.size() == changesCount

        where:
        leftList     | rightList    || changesCount
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
        def leftNode = buildGraph(dummyUser().withIntegerList(leftList as List).build())
        def rightNode = buildGraph(dummyUser().withIntegerList(rightList as List).build())

        def change = new ListChangeAppender(new MapChangeAppender(javers.typeMapper), javers.typeMapper).calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        change == null

        where:
        leftList | rightList
        []       | []
        [1, 2]   | [1, 2]
    }
}
