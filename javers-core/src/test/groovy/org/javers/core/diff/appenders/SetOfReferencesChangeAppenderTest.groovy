package org.javers.core.diff.appenders

import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity

import static org.javers.core.diff.appenders.ContainerChangeAssert.getAssertThat
import static org.javers.core.GlobalIdTestBuilder.instanceId
import static org.javers.core.GlobalIdTestBuilder.valueObjectId

/**
 * @author wioleta.gozdzik
 */
class SetOfReferencesChangeAppenderTest extends AbstractDiffAppendersTest {

    def "should append ValueAdded in Set of Entities"() {
        given:
        def leftCdo = new SnapshotEntity(id: 1, setOfEntities: [new SnapshotEntity(id: 2)])
        def rightCdo = new SnapshotEntity(id: 1, setOfEntities: [new SnapshotEntity(id: 2), new SnapshotEntity(id: 5)])

        when:
        def change = setChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "setOfEntities"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueAdded(instanceId(5, SnapshotEntity))
    }

    def "should append ValueRemoved in Set of Entities"() {
        given:
        def leftCdo = new SnapshotEntity(id: 1, setOfEntities: [new SnapshotEntity(id: 2), new SnapshotEntity(id: 5)])
        def rightCdo = new SnapshotEntity(id: 1, setOfEntities: [new SnapshotEntity(id: 2)])

        when:
        def change = setChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "setOfEntities"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueRemoved(instanceId(5, SnapshotEntity))
    }

    def "should NOT append ElementValueChange in Set of Entities"() {
        given:
        def leftCdo = new SnapshotEntity(id: 1, setOfEntities: [new SnapshotEntity(id: 2), new SnapshotEntity(id: 3)])
        def rightCdo = new SnapshotEntity(id: 1, setOfEntities: [new SnapshotEntity(id: 3), new SnapshotEntity(id: 2)])

        when:
        def change = setChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "setOfEntities"))

        then:
        !change
    }

    def "should support Set of ValueObjects for added/removed objects"() {
        given:
        def leftCdo =  new SnapshotEntity(setOfValueObjects: [new DummyAddress("London"), new DummyAddress("Tokyo")])
        def rightCdo = new SnapshotEntity(setOfValueObjects: [new DummyAddress("Paris"), new DummyAddress("London")])

        when:
        def change = setChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "setOfValueObjects"))

        then:
        assertThat(change)
                .hasSize(2)
                .hasValueAdded(valueObjectId(1, SnapshotEntity, "setOfValueObjects/"+javers.addressHash("Paris")))
                .hasValueRemoved(valueObjectId(1, SnapshotEntity, "setOfValueObjects/"+javers.addressHash("Tokyo")))
    }
}


