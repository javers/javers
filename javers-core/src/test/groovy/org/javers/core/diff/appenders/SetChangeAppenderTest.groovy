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
class SetChangeAppenderTest extends AbstractDiffTest {

    @Unroll
    def "should append #changesCount changes when left set is #leftSet and right set is #rightSet"() {
        given:
        def javers = javersTestAssembly()

        when:
        def leftNode = buildGraph(dummyUser().withStringsSet(leftSet as Set).build())
        def rightNode = buildGraph(dummyUser().withStringsSet(rightSet as Set).build())

        def change = new SetChangeAppender(javers.typeMapper).calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(DummyUser, "stringSet"))

        then:
        change.changes.size() == changesCount

        where:
        leftSet              | rightSet             || changesCount
        []                   | ["1", "2"]           || 2
        ["1", "2"]           | ["1", "2", "3", "4"] || 2
        ["1", "2"]           | ["2", "1", "3"]      || 1
        ["1", "2"]           | []                   || 2
        ["1", "2", "3", "4"] | ["1"]                || 3
        ["2", "1", "3"]      | ["1", "2"]           || 1
    }

    @Unroll
    def "should not append changes when left set #leftSet and right set #rightSet is equal"() {
        given:
        def javers = javersTestAssembly()

        when:
        def leftNode = buildGraph(dummyUser().withStringsSet(leftSet as Set).build())
        def rightNode = buildGraph(dummyUser().withStringsSet(rightSet as Set).build())

        def change = new SetChangeAppender(javers.typeMapper).calculateChanges(
                new RealNodePair(leftNode, rightNode), getProperty(DummyUser, "stringSet"))

        then:
        change == null

        where:
        leftSet | rightSet
        []             | []
        ["1", "2"]     | ["1", "2"]
        ["1", "2"]     | ["2", "1"]
    }
}