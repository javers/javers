package org.javers.core.diff.appenders

import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity

import static org.javers.core.GlobalIdTestBuilder.valueObjectId
import static org.javers.core.diff.appenders.ContainerChangeAssert.getAssertThat
import static org.javers.core.GlobalIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class ListReferenceChangeAppenderTest extends AbstractDiffAppendersTest {

    def "should append ElementReferenceChange in List of Entities"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def rightCdo = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])

        when:
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfEntities"))

        then:
        assertThat(change)
                  .hasSize(1)
                  .hasValueChange(1,instanceId(3, SnapshotEntity), instanceId(5, SnapshotEntity))
    }

    def "should NOT append ElementReferenceChange in List of ValueObject"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  listOfValueObjects:[new DummyAddress("London")])
        def rightCdo = new SnapshotEntity(id:1,  listOfValueObjects:[new DummyAddress("London","Street")])

        when:
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfValueObjects"))

        then:
        !change
    }

    def "should append ReferenceAdded in List of Entities"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2)])
        def rightCdo = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])

        when:
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfEntities"))

        then:
        assertThat(change)
                  .hasSize(1)
                  .hasValueAdded(1, instanceId(5, SnapshotEntity))
    }

    def "should append ReferenceRemoved in List of Entities"() {
        given:
        def leftCdo  = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])
        def rightCdo = new SnapshotEntity(id:1,  listOfEntities:[new SnapshotEntity(id:2)])

        when:
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfEntities"))

        then:
        assertThat(change)
                  .hasSize(1)
                  .hasValueRemoved(1, instanceId(5, SnapshotEntity))
    }

    def "should append ReferenceAdded in List of ValueObject"() {
        given:
        def leftCdo  = new SnapshotEntity(id:5,  listOfValueObjects:[new DummyAddress("London")])
        def rightCdo = new SnapshotEntity(id:5,  listOfValueObjects:[new DummyAddress("London"), new DummyAddress("London")])

        when:
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfValueObjects"))

        then:
        assertThat(change)
                  .hasSize(1)
                  .hasValueAdded(1, valueObjectId(5, SnapshotEntity, "listOfValueObjects/1"))
    }

    def "should append ReferenceRemoved in List of ValueObject"() {
        given:
        def leftCdo  = new SnapshotEntity(id:5,  listOfValueObjects:[new DummyAddress("London"), new DummyAddress("London")])
        def rightCdo = new SnapshotEntity(id:5,  listOfValueObjects:[new DummyAddress("London")])

        when:
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfValueObjects"))

        then:
        assertThat(change)
                  .hasSize(1)
                  .hasValueRemoved(1, valueObjectId(5, SnapshotEntity, "listOfValueObjects/1"))
    }

}
