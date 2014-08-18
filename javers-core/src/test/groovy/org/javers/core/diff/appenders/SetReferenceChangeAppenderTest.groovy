package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest;
import org.javers.core.model.SnapshotEntity

import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId;

import static org.javers.core.diff.appenders.ContainerChangeAssert.getAssertThat

/**
 * @author wioleta.gozdzik
 */

public class SetReferenceChangeAppenderTest extends AbstractDiffTest {

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
}


