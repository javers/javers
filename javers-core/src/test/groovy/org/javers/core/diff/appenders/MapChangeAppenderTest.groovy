package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.RealNodePair
import org.javers.core.diff.changetype.map.EntryAddOrRemove
import org.javers.core.diff.changetype.map.EntryAdded
import org.javers.core.diff.changetype.map.EntryRemoved
import org.javers.core.diff.changetype.map.EntryValueChanged
import org.javers.core.model.DummyUser
import org.javers.core.metamodel.property.Property
import org.javers.model.object.graph.ObjectNode
import org.joda.time.LocalDateTime
import spock.lang.Unroll
import static org.javers.core.diff.ChangeAssert.*

import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class MapChangeAppenderTest extends AbstractDiffTest{

    @Unroll
    def "should not append mapChanges when maps are #what" () {
        given:
        ObjectNode left =  buildGraph(dummyUser("1").withPrimitiveMap(leftMap).build())
        ObjectNode right = buildGraph(dummyUser("1").withPrimitiveMap(rightMap).build())
        Property valueMap = getEntity(DummyUser).getProperty("primitiveMap")

        expect:
        def changes = new MapChangeAppender().calculateChanges(new RealNodePair(left,right),valueMap)
        changes.size() == 0

        where:
        what << ["equal","null"]
        leftMap <<  [["some":1], null]
        rightMap << [["some":1], null]
    }

    def "should set MapChange metadata"() {
        given:
        ObjectNode left =  buildGraph(dummyUser("1").withPrimitiveMap(null).build())
        ObjectNode right = buildGraph(dummyUser("1").withPrimitiveMap(["some":1]).build())
        Property primitiveMap = getEntity(DummyUser).getProperty("primitiveMap")

        when:
        def changes =  new MapChangeAppender().calculateChanges(new RealNodePair(left,right),primitiveMap)

        then:
        assertThat(changes[0])
                    .hasProperty(primitiveMap)
                    .hasInstanceId(DummyUser, "1")
                    .hasAffectedCdo(right)
    }

    @Unroll
    def "should append #changeType.simpleName when left map is #leftMap and rightMap is #rightMap"() {
        given:
        ObjectNode left =  buildGraph(dummyUser("1").withPrimitiveMap(leftMap).build())
        ObjectNode right = buildGraph(dummyUser("1").withPrimitiveMap(rightMap).build())
        Property primitiveMap = getEntity(DummyUser).getProperty("primitiveMap")

        expect:
        def changes = new MapChangeAppender().calculateChanges(new RealNodePair(left,right),primitiveMap)
        changes.size() == 1
        EntryAddOrRemove entryAddOrRemove = changes[0].entryChanges[0]
        entryAddOrRemove.key == "some"
        entryAddOrRemove.value == 1
        entryAddOrRemove.class == changeType

        where:
        changeType << [EntryAdded,  EntryRemoved,  EntryAdded,           EntryRemoved]
        leftMap <<    [null,        ["some":1],    ["other":1],          ["some":1,"other":1] ]
        rightMap <<   [["some":1],   null,         ["other":1,"some":1], ["other":1] ]
    }

    def "should append EntryValueChanged when Primitive entry.value is changed"() {
        given:
        ObjectNode left =  buildGraph(dummyUser("1").withPrimitiveMap(["some":1,"other":2] ).build())
        ObjectNode right = buildGraph(dummyUser("1").withPrimitiveMap(["some":2,"other":2]).build())
        Property primitiveMap = getEntity(DummyUser).getProperty("primitiveMap")

        when:
        def changes =  new MapChangeAppender().calculateChanges(new RealNodePair(left,right),primitiveMap)

        then:
        changes.size() == 1
        EntryValueChanged entryValueChanged = changes[0].entryChanges[0]
        entryValueChanged.key == "some"
        entryValueChanged.leftValue == 1
        entryValueChanged.rightValue == 2
    }

    def "should append EntryValueChanged when ValueType entry.value is changed"() {

        def dayOne = new LocalDateTime(2000,1,1,12,1)
        def dayTwo = new LocalDateTime(2000,1,1,12,2)

        given:
        ObjectNode left =  buildGraph(dummyUser("1")
                          .withValueMap(["some":dayOne, "other":dayTwo] ).build())
        ObjectNode right = buildGraph(dummyUser("1")
                          .withValueMap(["some":dayTwo, "other":dayTwo]).build())
        Property valueMap = getEntity(DummyUser).getProperty("valueMap")

        when:
        def changes =  new MapChangeAppender().calculateChanges(new RealNodePair(left,right),valueMap)

        then:
        changes.size() == 1
        EntryValueChanged entryValueChanged = changes[0].entryChanges[0]
        entryValueChanged.key == "some"
        entryValueChanged.leftValue ==   dayOne
        entryValueChanged.rightValue ==  dayTwo
    }
}
