package org.javers.core.snapshot

import org.javers.core.JaversTestBuilder
import org.javers.core.model.SnapshotEntity
import org.javers.core.graph.NodeAssert
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.GlobalIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class SnapshotGraphFactoryTest extends Specification {

    @Shared JaversTestBuilder javers
    @Shared SnapshotGraphFactory snapshotGraphFactory

    def setup(){
        javers = JaversTestBuilder.javersTestAssembly()
        snapshotGraphFactory = javers.getContainerComponent(SnapshotGraphFactory)
    }

    def "should create SnapshotGraph with snapshots of committed objects "() {
        given:
        def oldRef = new SnapshotEntity(id: 2, intProperty:2)
        javers.javers().commit("user",oldRef)
        def cdo = new SnapshotEntity(id: 1, entityRef: oldRef)
        def liveGraph = javers.createLiveGraph(cdo)

        when:
        def snapshotGraph = snapshotGraphFactory.createLatest(liveGraph.globalIds())

        then:
        snapshotGraph.nodes().size() == 1
        NodeAssert.assertThat(snapshotGraph.nodes()[0]).hasGlobalId(instanceId(2,SnapshotEntity))
                  .isSnapshot()
    }
}
