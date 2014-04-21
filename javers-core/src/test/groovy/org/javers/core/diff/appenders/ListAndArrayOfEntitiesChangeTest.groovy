package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.RealNodePair
import org.javers.core.diff.changetype.ContainerChange
import org.javers.core.model.SnapshotEntity
import spock.lang.Unroll

/**
 * @author bartosz walacik
 */
class ListAndArrayOfEntitiesChangeTest extends AbstractDiffTest {

    @Unroll
    def "should compare #containerType of Entities using GlobalId"() {

        when:
        def change = listChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "listOfEntities"))

        then:
        ContainerChangeAssert.assertThat(change).hasSize(1)

        where:
        containerType << ["Lists"]*1 +["Arrays"]*1
        leftCdo <<  [new SnapshotEntity(id:1, listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:3)])]
        rightCdo << [new SnapshotEntity(id:1, listOfEntities:[new SnapshotEntity(id:2), new SnapshotEntity(id:5)])]
    }

    RealNodePair realNodePair(def leftCdo, def rightCdo){
        new RealNodePair(buildGraph(leftCdo), buildGraph(rightCdo))
    }

    ListChangeAppender listChangeAppender() {
        new ListChangeAppender(new MapChangeAppender(javers.typeMapper), javers.typeMapper)
    }
}
