package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.RealNodePair
import org.javers.core.model.DummyUser

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.test.builder.DummyUserBuilder.dummyUser


class SetChangeAppenderTest extends AbstractDiffTest {

    def "should append ValueAdd change in #leftCollection and #rightCollection"() {
        given:
        def javers = javersTestAssembly()

        when:
        def leftNode = buildGraph(dummyUser().withStringsSet(leftCollection as Set).build())
        def rightNode = buildGraph(dummyUser().withStringsSet(rightCollection as Set).build())

        def changes = new SetChangeAppender(javers.typeMapper).calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(DummyUser, "stringSet"))

        then:
        changes.size() == changesCount

        where:
        leftCollection       | rightCollection      || changesCount
        []                   | ["1", "2"]           || 2
        ["1", "2"]           | ["1", "2", "3", "4"] || 2
        ["1", "2"]           | ["2", "1", "3"]      || 1
        ["1", "2"]           | []                   || 2
        ["1", "2", "3", "4"] | ["1"]                || 3
        ["2", "1", "3"]      | ["1", "2"]           || 1
        []                   | []                   || 0
        ["1", "2"]           | ["1", "2"]           || 0
        ["1", "2"]           | ["2", "1"]           || 0
    }
}