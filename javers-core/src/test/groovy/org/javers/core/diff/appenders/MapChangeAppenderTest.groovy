package org.javers.core.diff.appenders

import com.google.gson.reflect.TypeToken
import org.javers.common.exception.JaversException
import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.map.EntryAddOrRemove
import org.javers.core.diff.changetype.map.EntryAdded
import org.javers.core.diff.changetype.map.EntryRemoved
import org.javers.core.diff.changetype.map.EntryValueChange
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.property.Property
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.repository.jql.QueryBuilder

import java.time.LocalDateTime
import spock.lang.Unroll

import static org.javers.common.exception.JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
import static org.javers.core.JaversBuilder.javers
import static org.javers.core.diff.ChangeAssert.assertThat
import static org.javers.core.model.DummyUser.dummyUser

/**
 * @author bartosz walacik
 */
class MapChangeAppenderTest extends AbstractDiffAppendersTest {

    enum En {
        A,B
    }
    class Entity {
        @Id
        int id
        Map<DummyAddress, String> mapWithValueObjectKey
        Map<Entity, String> mapWithEntityKey
        Map<En, String> mapWithValueKey
        Map<String, String> mapWithPrimitiveKey
    }

    def "should not support Maps with ValueObject as keys"() {
        when:
        new MapChangeAppender().supports(getJaversType(new TypeToken<Map<DummyAddress, String>>() {}.getType()))

        then:
        def e = thrown(JaversException)
        e.code == VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
    }

    def "should support Maps with various types of keys"() {
        given:
        def javers = javers().build()

        def a = new Entity(id:1)
        def b = new Entity(id:1,
                mapWithValueKey: [(En.A):"a"],
                mapWithPrimitiveKey: ["a":"b"],
                mapWithEntityKey: [(new Entity(id:2)):"b"]
        )

        when:
        def diff = javers.compare(a,b)

        then:
        with(diff.getPropertyChanges('mapWithValueKey')[0].changes[0]) {
            it.key == En.A
            it.value == "a"
        }
        with(diff.getPropertyChanges('mapWithPrimitiveKey')[0].changes[0]) {
            it.key == "a"
            it.value == "b"
        }
        with(diff.getPropertyChanges('mapWithEntityKey')[0].changes[0]) {
            it.key instanceof InstanceId
            it.key.value().endsWith('MapChangeAppenderTest$Entity/2')
            it.value == "b"
        }
    }

    @Unroll
    def "should not append mapChanges when maps are #what" () {
        given:
        def left =  dummyUser("1").withPrimitiveMap(leftMap)
        def right = dummyUser("1").withPrimitiveMap(rightMap)
        def valueMap = getEntity(DummyUser).getProperty("primitiveMap")

        expect:
        def change = new MapChangeAppender().calculateChanges(realNodePair(left,right),valueMap)
        change == null

        where:
        what << ["equal","null"]
        leftMap <<  [["some":1], null]
        rightMap << [["some":1], null]
    }

    def "should set MapChange metadata"() {
        given:
        def left =  dummyUser("1").withPrimitiveMap(null)
        def right = dummyUser("1").withPrimitiveMap(["some":1])
        def primitiveMap = getEntity(DummyUser).getProperty("primitiveMap")

        when:
        def change = new MapChangeAppender().calculateChanges(realNodePair(left,right),primitiveMap)

        then:
        assertThat(change)
                    .hasPropertyName("primitiveMap")
                    .hasInstanceId(DummyUser, "1")
    }

    @Unroll
    def "should append #changeType.simpleName when left map is #leftMap and rightMap is #rightMap"() {
        given:
        def left =  dummyUser("1").withPrimitiveMap(leftMap)
        def right = dummyUser("1").withPrimitiveMap(rightMap)
        Property primitiveMap = getEntity(DummyUser).getProperty("primitiveMap")

        expect:
        def change = new MapChangeAppender().calculateChanges(realNodePair(left,right),primitiveMap)
        EntryAddOrRemove entryAddOrRemove = change.entryChanges[0]
        entryAddOrRemove.key == "some"
        entryAddOrRemove.value == 1
        entryAddOrRemove.class == changeType

        where:
        changeType << [EntryAdded,  EntryRemoved,  EntryAdded,           EntryRemoved]
        leftMap <<    [null,        ["some":1],    ["other":1],          ["some":1,"other":1] ]
        rightMap <<   [["some":1],   null,         ["other":1,"some":1], ["other":1] ]
    }

    def "should append EntryValueChanged when Primitive value is changed"() {
        given:
        def left =  dummyUser("1").withPrimitiveMap(["some":1,"other":2] )
        def right = dummyUser("1").withPrimitiveMap(["some":2,"other":2])
        Property primitiveMap = getEntity(DummyUser).getProperty("primitiveMap")

        when:
        def change = new MapChangeAppender().calculateChanges(realNodePair(left,right),primitiveMap)

        then:
        EntryValueChange entryValueChanged = change.entryChanges[0]
        entryValueChanged.key == "some"
        entryValueChanged.leftValue == 1
        entryValueChanged.rightValue == 2
    }

    def "should append EntryValueChanged when ValueType value is changed"() {

        def dayOne = LocalDateTime.of(2000,1,1,12,1)
        def dayTwo = LocalDateTime.of(2000,1,1,12,2)

        given:
        def left =  dummyUser("1").withValueMap(["some":dayOne, "other":dayTwo] )
        def right = dummyUser("1").withValueMap(["some":dayTwo, "other":dayTwo])
        Property valueMap = getEntity(DummyUser).getProperty("valueMap")

        when:
        def change = new MapChangeAppender().calculateChanges(realNodePair(left,right),valueMap)

        then:
        EntryValueChange entryValueChanged = change.entryChanges[0]
        entryValueChanged.key == "some"
        entryValueChanged.leftValue ==   dayOne
        entryValueChanged.rightValue ==  dayTwo
    }


    class E {
        String profile
        List<String> roles

        E(String profile, List<String> roles) {
            this.profile = profile
            this.roles = roles
        }
    }
    class A {
        Map<String, List<E>> map
        List<E> list
    }

    def "should support Map from String to List of ValueObjects" () {
        given:
        def javers = JaversBuilder.javers().build()

        def map = [
                "key1" : [ new E("p1", ["teacher", "director"]),
                           new E("p3", ["manager", "director"])
                ],
                "key2" : [ new E("p2", ["student", "pupil"]) ]
        ]

        def a = new A(map: map, list: [new E("p1", ["teacher", "director"])])

        when:
        javers.commit("a", a)
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(A).build())

        def mapSnapshot = snapshots[0].getPropertyValue('map')

        then:
        mapSnapshot['key1'][0].value().endsWith('MapChangeAppenderTest$A/#map/key1/0')
        mapSnapshot['key1'][1].value().endsWith('MapChangeAppenderTest$A/#map/key1/1')
        mapSnapshot['key2'][0].value().endsWith('MapChangeAppenderTest$A/#map/key2/0')

        when:
        def voSnapshots = javers.findSnapshots(QueryBuilder.byClass(E).build())

        then:
        voSnapshots.size() == 4
        def e1Snapshot = voSnapshots.find{it.globalId.value().endsWith('MapChangeAppenderTest$A/#map/key1/0')}
        e1Snapshot.getPropertyValue('profile') == 'p1'
    }
}
