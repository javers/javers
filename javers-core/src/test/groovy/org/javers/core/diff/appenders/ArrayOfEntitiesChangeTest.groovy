package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.metamodel.object.ValueObjectId
import org.javers.core.model.SnapshotEntity

import static org.javers.core.metamodel.object.InstanceId.InstanceIdDTO.instanceId

/**
 * @author bartosz walacik
 */
class ArrayOfEntitiesChangeTest extends AbstractDiffTest {
    def "should append ReferenceChanged in Array of Entities"() {
        when:
        def leftCdo  = new SnapshotEntity(id:1,  arrayOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def rightCdo = new SnapshotEntity(id:1,  arrayOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])
        def change = arrayChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "arrayOfEntities"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasReferenceChange(1,instanceId(3, SnapshotEntity), instanceId(5, SnapshotEntity))
    }

    def "should append ReferenceAdded in Array of Entities"() {
        when:
        def leftCdo  = new SnapshotEntity(id:1,  arrayOfEntities:[new SnapshotEntity(id:2)])
        def rightCdo = new SnapshotEntity(id:1,  arrayOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])
        def change = arrayChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "arrayOfEntities"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasReferenceAdded(1, instanceId(5, SnapshotEntity))
    }

    def "should append ReferenceRemoved in Array of Entities"() {
        when:
        def leftCdo  = new SnapshotEntity(id:1,  arrayOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])
        def rightCdo = new SnapshotEntity(id:1,  arrayOfEntities:[new SnapshotEntity(id:2)])
        def change = arrayChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "arrayOfEntities"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasReferenceRemoved(1, instanceId(5, SnapshotEntity))
    }

    def "should append ReferenceAdded in Array of ValueObject"() {
        when:
        def leftCdo  = new SnapshotEntity(id:1,  arrayOfValueObjects:[new SnapshotEntity(id:2)])
        def rightCdo = new SnapshotEntity(id:1,  arrayOfValueObjects:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])
        def change = arrayChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "arrayOfValueObjects"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasReferenceAdded(1, ValueObjectId.ValueObjectIdDTO.valueObjectId(5, SnapshotEntity, "arrayOfValueObjects/1"))
    }

    def "should append ReferenceRemoved in Array of ValueObject"() {
        when:
        def leftCdo  = new SnapshotEntity(id:1,  arrayOfValueObjects:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])
        def rightCdo = new SnapshotEntity(id:1,  arrayOfValueObjects:[new SnapshotEntity(id:2)])
        def change = arrayChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "arrayOfValueObjects"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasReferenceRemoved(1, ValueObjectId.ValueObjectIdDTO.valueObjectId(5, SnapshotEntity, "arrayOfValueObjects/1"))
    }
}
