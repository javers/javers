package org.javers.core.diff.appenders

import org.javers.common.exception.exceptions.JaversException
import org.javers.core.diff.AbstractDiffTest
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity

import static org.javers.common.exception.exceptions.JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
import static org.javers.core.metamodel.object.InstanceId.InstanceIdDTO.instanceId

/**
 * @author bartosz walacik
 */
public class MapReferenceChangeAppenderTest extends AbstractDiffTest {
    //value changes: 1...

    def "should append EntryValueChange for Map<Entity,Entity>"() {

    }

    def "should append EntryValueChange for Map<Entity,Primitive>"() {

    }

    def "should append EntryValueChange for Map<Primitive,Entity>"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  mapPrimitiveToEntity: [a:new SnapshotEntity(id:2), b:new SnapshotEntity(id:3)])
        def rightCdo = new SnapshotEntity(id:1,  mapPrimitiveToEntity: [a:new SnapshotEntity(id:2), b:new SnapshotEntity(id:5)])

        when:
        def change = mapChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "mapPrimitiveToEntity"))

        then:
        MapChangeAssert.assertThat(change)
                .hasSize(1)
                .hasEntryValueChange("b",instanceId(3, SnapshotEntity), instanceId(5, SnapshotEntity))
    }

    def "should not support Map<ValueObject,?>, no good idea how to handle this"() {
        given:
            def leftCdo  = new SnapshotEntity(id:1,  mapVoToPrimitive: [(new DummyAddress("London")):1])
            def rightCdo = new SnapshotEntity(id:1,  mapVoToPrimitive: [(new DummyAddress("London")):2])
        when:
            def change = mapChangeAppender()
                        .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "mapVoToPrimitive"))

        then:
            def e = thrown(JaversException)
            e.code == VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
    }

    def "should NOT append EntryValueChange for Map<?,ValueObject>"() {
        given:
            def leftCdo  = new SnapshotEntity(id:1,  mapPrimitiveToVO: [a: new DummyAddress("London")])
            def rightCdo = new SnapshotEntity(id:1,  mapPrimitiveToVO: [a: new DummyAddress("London","Street")])

        when:
             def change = mapChangeAppender()
                         .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "mapPrimitiveToVO"))

        then:
            !change
    }
}
