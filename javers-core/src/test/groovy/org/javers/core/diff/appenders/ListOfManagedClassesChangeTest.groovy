package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.metamodel.object.ValueObjectId
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity

import static org.javers.core.metamodel.object.InstanceId.InstanceIdDTO.instanceId

/**
 * @author bartosz walacik
 */
class ListOfManagedClassesChangeTest extends AbstractDiffTest {

    def "should append ReferenceChanged in List of Entities"() {
        when:
        def leftCdo  = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def rightCdo = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfEntities"))

        then:
        ContainerChangeAssert.assertThat(change)
                             .hasSize(1)
                             .hasReferenceChange(1,instanceId(3, SnapshotEntity), instanceId(5, SnapshotEntity))
    }

    def "should append ReferenceAdded in List of Entities"() {
        when:
        def leftCdo  = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2)])
        def rightCdo = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])
        def change = listChangeAppender()
                     .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfEntities"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasReferenceAdded(1, instanceId(5, SnapshotEntity))
    }

    def "should append ReferenceRemoved in List of Entities"() {
        when:
        def leftCdo  = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])
        def rightCdo = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2)])
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfEntities"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasReferenceRemoved(1, instanceId(5, SnapshotEntity))
    }

    def "should append ReferenceAdded in List of ValueObject"() {
        when:
        def leftCdo  = new SnapshotEntity(id:1,  listOfValueObjects:[new DummyAddress("London")])
        def rightCdo = new SnapshotEntity(id:1,  listOfValueObjects:[new DummyAddress("London"), new DummyAddress("London")])
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfValueObjects"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasReferenceAdded(1, ValueObjectId.ValueObjectIdDTO.valueObjectId(5, SnapshotEntity, "listOfValueObjects/1"))
    }

    def "should append ReferenceRemoved in List of ValueObject"() {
        when:
        def leftCdo  = new SnapshotEntity(id:1,  listOfValueObjects:[new DummyAddress("London"), new DummyAddress("London")])
        def rightCdo = new SnapshotEntity(id:1,  listOfValueObjects:[new DummyAddress("London")])
        def change = listChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfValueObjects"))

        then:
        ContainerChangeAssert.assertThat(change)
                .hasSize(1)
                .hasReferenceRemoved(1, ValueObjectId.ValueObjectIdDTO.valueObjectId(5, SnapshotEntity, "listOfValueObjects/1"))
    }

    def "should not append ReferenceChanged in List of ValueObject"() {
        when:
        def leftCdo  = new SnapshotEntity(id:1,  listOfValueObjects:[new DummyAddress("London")])
        def rightCdo = new SnapshotEntity(id:1,  listOfValueObjects:[new DummyAddress("London","Street")])
        def change = listChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfValueObjects"))

        then:
        !change
    }
}
