package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.RealNodePair
import org.javers.core.model.DummyUser

import static org.javers.test.builder.DummyUserBuilder.dummyUser


class CollectionValueChangeAppenderTest extends AbstractDiffTest {

    def appender = new ValueAddedAppender()

    def "should append ValueAdd change in #leftCollection and #rightCollection"() {
        when:
        def changes = appender.calculateChanges(new RealNodePair(buildGraph(leftCollection.build()),
                buildGraph(rightCollection.build())), getProperty(DummyUser, property))

        then:
        changes.size() == added

        where:
        leftCollection | rightCollection | property || added
//        dummyUser().withStringsSet()         | dummyUser().withStringsSet("0", "1")           | "stringSet"   || 2
//        dummyUser().withStringsSet("0")      | dummyUser().withStringsSet("0", "1", "4")      | "stringSet"   || 2
//        dummyUser().withStringsSet("0")      | dummyUser().withStringsSet("0", "0", "0", "1") | "stringSet"   || 1
//        dummyUser().withStringsSet()         | dummyUser().withStringsSet()                   | "stringSet"   || 0
//        dummyUser().withStringsSet("0")      | dummyUser().withStringsSet("0")                | "stringSet"   || 0
//        dummyUser().withStringsSet("1", "0") | dummyUser().withStringsSet("0", "1")           | "stringSet"   || 0
//
//        dummyUser().withIntegerList()        | dummyUser().withIntegerList(1, 2, 2, 2)        | "integerList" || 4
        dummyUser().withIntegerList(1, 2) | dummyUser().withIntegerList(1, 2, 2, 2) | "integerList" || 2
        dummyUser().withIntegerList(1, 2) | dummyUser().withIntegerList(2, 1)       | "integerList" || 4
        dummyUser().withIntegerList(1, 2) | dummyUser().withIntegerList(2, 1, 2, 2) | "integerList" || 6
//        dummyUser().withIntegerList(1, 1, 1, 2, 2) | dummyUser().withIntegerList(1, 2, 3, 3, 3)     | "integerList" || 3
    }
}