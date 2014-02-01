package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.RealNodePair
import org.javers.core.diff.changetype.ValueRemoved
import org.javers.core.model.DummyUser

import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * pawel szymczyk
 */
class ValueRemovedAppenderTest extends AbstractDiffTest{

    def "should append ValueRemoved change in different sets"() {
        given:
        def userV1 = buildGraph(dummyUser("kazik").withStringsSet("s1", "s2", "s3").build())
        def userV2 = buildGraph(dummyUser("kazik").withStringsSet("s1", "s2",).build())

        when:
        def changes = new ValueRemovedAppender().calculateChanges(new RealNodePair(userV1, userV2), getProperty(DummyUser, "stringSet"))

        then:
        changes.size() == 1
        changes[0] instanceof ValueRemoved
        changes[0].removedValue == "s3"
    }

    def "should append ValueRemoved change in different lists"() {
        given:
        def userV1 = buildGraph(dummyUser("kazik").withIntegerList(1, 2, 3).build())
        def userV2 = buildGraph(dummyUser("kazik").withIntegerList().build())

        when:
        def changes = new ValueRemovedAppender().calculateChanges(new RealNodePair(userV1, userV2), getProperty(DummyUser, "integerList"))

        then:
        changes.size() == 3
        changes.collect { it.removedValue }.containsAll(1, 2, 3)
    }

    def "should append ValueRemoved change in different lists with duplicates"() {
        given:
        def userV1 = buildGraph(dummyUser("kazik").withIntegerList(1, 2, 3).build())
        def userV2 = buildGraph(dummyUser("kazik").withIntegerList(1, 1, 1, 2, 2, 2, 2).build())

        when:
        def changes = new ValueRemovedAppender().calculateChanges(new RealNodePair(userV1, userV2), getProperty(DummyUser, "integerList"))

        then:
        changes.size() == 1
        changes[0] instanceof ValueRemoved
        changes[0].removedValue == 3
    }

    def "should append ValueRemoved change in different arrays"() {
        given:
        def userV1 = buildGraph(dummyUser("kazik").withIntArray(1, 2, 3, 4, 5, 6).build())
        def userV2 = buildGraph(dummyUser("kazik").withIntArray(1, 2).build())

        when:
        def changes = new ValueRemovedAppender().calculateChanges(new RealNodePair(userV1, userV2), getProperty(DummyUser, "intArray"))

        then:
        changes.size() == 4
        changes.collect { it.removedValue }.containsAll( 3, 4, 5, 6 )
    }

    def "should append ValueRemoved change in different arrays with duplicates"() {
        given:
        def userV1 = buildGraph(dummyUser("kazik").withIntArray(1, 2, 3).build())
        def userV2 = buildGraph(dummyUser("kazik").withIntArray(1, 1, 1, 2, 2, 2).build())

        when:
        def changes = new ValueRemovedAppender().calculateChanges(new RealNodePair(userV1, userV2), getProperty(DummyUser, "intArray"))

        then:
        changes.size() == 1
        changes[0] instanceof ValueRemoved
        changes[0].removedValue == 3
    }

    def "should not append ValueRemoved change when the same values in containers"() {
        when:
        def changes = new ValueRemovedAppender().calculateChanges(new RealNodePair(buildGraph(userV1.build()), buildGraph(userV2.build())),
                getProperty(DummyUser,
                        propertyName))

        then:
        changes.isEmpty()

        where:
        userV1                                           | userV2                                           | propertyName
        dummyUser("kazik").withIntArray(1, 2, 3)         | dummyUser("kazik").withIntArray(1, 2, 3)         | "intArray"
        dummyUser("kazik").withStringsSet("4", "5", "6") | dummyUser("kazik").withStringsSet("4", "5", "6") | "stringSet"
        dummyUser("kazik").withIntegerList(7, 8, 9)      | dummyUser("kazik").withIntegerList(7, 8, 9)      | "integerList"
    }

    def "should not append ValueRemoved for empty containers"() {
        when:
        def changes = new ValueRemovedAppender().calculateChanges(new RealNodePair(buildGraph(userV1.build()), buildGraph(userV2.build())),
                getProperty(DummyUser,
                        propertyName))

        then:
        changes.isEmpty()

        where:
        userV1                               | userV2                               | propertyName
        dummyUser("kazik").withIntArray()    | dummyUser("kazik").withIntArray()    | "intArray"
        dummyUser("kazik").withStringsSet()  | dummyUser("kazik").withStringsSet()  | "stringSet"
        dummyUser("kazik").withIntegerList() | dummyUser("kazik").withIntegerList() | "integerList"
    }
}
