package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.RealNodePair
import org.javers.core.model.DummyUser

import java.lang.reflect.Array

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.test.builder.DummyUserBuilder.dummyUser


class ListChangeAppenderTest extends AbstractDiffTest {

    def "should append ValueAdd change in #leftCollection and #rightCollection"() {
        given:
        def javers = javersTestAssembly()

        when:
        def leftNode = buildGraph(dummyUser().withIntegerList(leftCollection as List).build())
        def rightNode = buildGraph(dummyUser().withIntegerList(rightCollection as List).build())

        def changes = new ListChangeAppender(new MapChangeAppender(javers.typeMapper)).calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        changes.size() == changesCount

        where:
        leftCollection | rightCollection || changesCount
        []             | [1, 2, 2, 2]    || 4
        [1, 2]         | [1, 2, 3, 4]    || 2
        [1, 2]         | [1, 2, 2, 2]    || 2
        [1, 2]         | [2, 1]          || 2
        [1, 2]         | [2, 1, 2, 3]    || 4
        [1, 2, 2, 2]   | []              || 4
        [1, 2, 3, 4]   | [1, 2]          || 2
        [1, 2, 2, 2]   | [1, 2]          || 2
        [2, 1, 2, 3]   | [1, 2]          || 4
        []             | []              || 0
        [1, 2]         | [1, 2]          || 0
    }

    def "should append ValueAdd change in #leftCollection and #rightCollection array"() {
        given:
        def javers = javersTestAssembly()

        when:
        def leftNode = buildGraph(dummyUser().withIntArray(leftCollection as int[]).build())
        def rightNode = buildGraph(dummyUser().withIntArray(rightCollection as int[]).build())

        def changes = new ArrayChangeAppender(new ListChangeAppender(new MapChangeAppender(javers.typeMapper))).calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(DummyUser, "intArray"))

        then:
        changes.size() == changesCount

        where:
        leftCollection | rightCollection || changesCount
        []             | [1, 2, 2, 2]    || 4
        [1, 2]         | [1, 2, 3, 4]    || 2
        [1, 2]         | [1, 2, 2, 2]    || 2
        [1, 2]         | [2, 1]          || 2
        [1, 2]         | [2, 1, 2, 3]    || 4
        [1, 2, 2, 2]   | []              || 4
        [1, 2, 3, 4]   | [1, 2]          || 2
        [1, 2, 2, 2]   | [1, 2]          || 2
        [2, 1, 2, 3]   | [1, 2]          || 4
        []             | []              || 0
        [1, 2]         | [1, 2]          || 0
    }
}
