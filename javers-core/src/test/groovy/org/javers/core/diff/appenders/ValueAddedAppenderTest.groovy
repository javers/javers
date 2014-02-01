package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.RealNodePair
import org.javers.core.diff.changetype.ValueAdded
import org.javers.core.model.DummyUser

import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author pawel szymczyk
 */
class ValueAddedAppenderTest extends AbstractDiffTest{

    def "should append ValueAdded change in different sets"() {
        given:
        def userV1 = buildGraph(dummyUser("kazik").withStringsSet("s1", "s2").build())
        def userV2 = buildGraph(dummyUser("kazik").withStringsSet("s1", "s2", "s3").build())

        when:
        def changes = new ValueAddedAppender().calculateChanges(new RealNodePair(userV1, userV2), getProperty(DummyUser, "stringSet"))

        then:
        changes.size() == 1
        changes[0] instanceof ValueAdded
        changes[0].addedValue == "s3"
    }

    def "should append ValueAdded change in different lists"() {
        given:
        def userV1 = buildGraph(dummyUser("kazik").withIntegerList().build())
        def userV2 = buildGraph(dummyUser("kazik").withIntegerList(1, 2, 3).build())

        when:
        def changes = new ValueAddedAppender().calculateChanges(new RealNodePair(userV1, userV2), getProperty(DummyUser, "integerList"))

        then:
        changes.size() == 3
        changes.collect { it.addedValue }.containsAll(1, 2, 3)
    }

    def "should append ValueAdded change in different lists with duplicates"() {
        given:
        def userV1 = buildGraph(dummyUser("kazik").withIntegerList(1, 1, 1, 2, 2, 2, 2).build())
        def userV2 = buildGraph(dummyUser("kazik").withIntegerList(1, 2, 3).build())

        when:
        def changes = new ValueAddedAppender().calculateChanges(new RealNodePair(userV1, userV2), getProperty(DummyUser, "integerList"))

        then:
        changes.size() == 1
        changes[0] instanceof ValueAdded
        changes[0].addedValue == 3
    }

    def "should append ValueAdded change in different arrays"() {
        given:
        def userV1 = buildGraph(dummyUser("kazik").withIntArray(1, 2).build())
        def userV2 = buildGraph(dummyUser("kazik").withIntArray(1, 2, 3, 4, 5, 6).build())

        when:
        def changes = new ValueAddedAppender().calculateChanges(new RealNodePair(userV1, userV2), getProperty(DummyUser, "intArray"))

        then:
        changes.size() == 4
        changes.collect { it.addedValue }.containsAll( 3, 4, 5, 6 )
    }

    def "should append ValueAdded change in different arrays with duplicates"() {
        given:
        def userV1 = buildGraph(dummyUser("kazik").withIntArray(1, 1, 1, 2, 2, 2).build())
        def userV2 = buildGraph(dummyUser("kazik").withIntArray(1, 2, 3).build())

        when:
        def changes = new ValueAddedAppender().calculateChanges(new RealNodePair(userV1, userV2), getProperty(DummyUser, "intArray"))

        then:
        changes.size() == 1
        changes[0] instanceof ValueAdded
        changes[0].addedValue == 3
    }

    def "should not append ValueAdded change when the same values in containers"() {
        when:
        def changes = new ValueAddedAppender().calculateChanges(new RealNodePair(buildGraph(userV1.build()), buildGraph(userV2.build())),
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

    def "should not append ValueAdded for empty containers"() {
        when:
        def changes = new ValueAddedAppender().calculateChanges(new RealNodePair(buildGraph(userV1.build()), buildGraph(userV2.build())),
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
