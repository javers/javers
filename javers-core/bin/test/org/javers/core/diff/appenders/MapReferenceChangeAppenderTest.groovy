package org.javers.core.diff.appenders

import com.google.gson.reflect.TypeToken
import org.javers.common.exception.JaversException
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import spock.lang.Unroll

import static org.javers.common.exception.JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
import static org.javers.core.diff.appenders.MapChangeAssert.getAssertThat
import static org.javers.core.GlobalIdTestBuilder.instanceId
import static org.javers.core.GlobalIdTestBuilder.valueObjectId

/**
 * @author bartosz walacik
 */
public class MapReferenceChangeAppenderTest extends AbstractDiffAppendersTest {

    @Unroll
    def "should append EntryAdded for #what"() {
        when:
        def change = mapChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, property))

        then:
        assertThat(change).hasSize(1)
                          .hasEntryAdded(expectedKey, expectedVal)

        where:
        what <<        ["Map<Entity,Entity>", "Map<Primitive,Entity>", "Map<Primitive,ValueObject>"]
        leftCdo <<     [new SnapshotEntity(id:1)] * 3
        rightCdo <<    [new SnapshotEntity(id:1,  mapOfEntities:        [(new SnapshotEntity(id:2)): new SnapshotEntity(id:3)]),
                        new SnapshotEntity(id:1,  mapPrimitiveToEntity: [a: new SnapshotEntity(id:2)]),
                        new SnapshotEntity(id:1,  mapPrimitiveToVO:     [a: new DummyAddress("London")]) ]
        property <<    ["mapOfEntities", "mapPrimitiveToEntity", "mapPrimitiveToVO" ]
        expectedKey << [instanceId(2, SnapshotEntity),"a","a"]
        expectedVal << [instanceId(3, SnapshotEntity),
                        instanceId(2, SnapshotEntity),
                        valueObjectId(1,SnapshotEntity,"mapPrimitiveToVO/a")]
    }

    @Unroll
    def "should append EntryRemoved for #what"() {
        when:
        def change = mapChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, property))

        then:
        assertThat(change).hasSize(1)
                          .hasEntryRemoved(expectedKey, expectedVal)

        where:
        what <<        ["Map<Entity,Entity>", "Map<Primitive,Entity>", "Map<Primitive,ValueObject>"]
        leftCdo <<     [new SnapshotEntity(id:1,  mapOfEntities:        [(new SnapshotEntity(id:2)): new SnapshotEntity(id:3)]),
                        new SnapshotEntity(id:1,  mapPrimitiveToEntity: [a: new SnapshotEntity(id:2)]),
                        new SnapshotEntity(id:1,  mapPrimitiveToVO:     [a: new DummyAddress("London")]) ]
        rightCdo <<    [new SnapshotEntity(id:1)] * 3
        property <<    ["mapOfEntities", "mapPrimitiveToEntity", "mapPrimitiveToVO" ]
        expectedKey << [instanceId(2, SnapshotEntity),"a","a"]
        expectedVal << [instanceId(3, SnapshotEntity),
                        instanceId(2, SnapshotEntity),
                        valueObjectId(1,SnapshotEntity,"mapPrimitiveToVO/a")]
    }

    def "should append EntryValueChange for Map of Entity to Entity"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  mapOfEntities: [(new SnapshotEntity(id:2)): new SnapshotEntity(id:3)])
        def rightCdo = new SnapshotEntity(id:1,  mapOfEntities: [(new SnapshotEntity(id:2)): new SnapshotEntity(id:5)])

        when:
        def change = mapChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "mapOfEntities"))

        then:
        assertThat(change).hasSize(1)
                          .hasEntryValueChange(instanceId(2, SnapshotEntity),
                                               instanceId(3, SnapshotEntity), instanceId(5, SnapshotEntity))
    }

    def "should append EntryValueChange for Map of Primitive to Entity"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  mapPrimitiveToEntity: [a:new SnapshotEntity(id:2), b:new SnapshotEntity(id:3)])
        def rightCdo = new SnapshotEntity(id:1,  mapPrimitiveToEntity: [a:new SnapshotEntity(id:2), b:new SnapshotEntity(id:5)])

        when:
        def change = mapChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "mapPrimitiveToEntity"))

        then:
        assertThat(change).hasSize(1)
                          .hasEntryValueChange("b",instanceId(3, SnapshotEntity), instanceId(5, SnapshotEntity))
    }

    def "should not support Map of ValueObject to ?, no good idea how to handle this"() {
        when:
            mapChangeAppender().supports(getJaversType(new TypeToken<Map<DummyAddress, String>>(){}.getType()))

        then:
            def e = thrown(JaversException)
            e.code == VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
    }

    def "should NOT append EntryValueChange for Map of ? to ValueObject"() {
        given:
            def leftCdo  = new SnapshotEntity(id:1,  mapPrimitiveToVO: [a: new DummyAddress("London")])
            def rightCdo = new SnapshotEntity(id:1,  mapPrimitiveToVO: [a: new DummyAddress("London","Street")])

        when:
             def change = mapChangeAppender()
                         .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "mapPrimitiveToVO"))

        then:
            !change
    }


    def "should EntryAdded & EntryRemoved when key is changed"() {
        given:
            def leftCdo  =  new SnapshotEntity(id:1,  mapOfEntities: [(new SnapshotEntity(id:10)): new SnapshotEntity(id:5)])
            def rightCdo  = new SnapshotEntity(id:1,  mapOfEntities: [(new SnapshotEntity(id:12)): new SnapshotEntity(id:5)])

        when:
          def change = mapChangeAppender()
                      .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "mapOfEntities"))

        then:
            assertThat(change).hasSize(2)
                              .hasEntryRemoved(instanceId(10,SnapshotEntity),instanceId(5,SnapshotEntity))
                              .hasEntryAdded  (instanceId(12,SnapshotEntity),instanceId(5,SnapshotEntity))
    }
}
