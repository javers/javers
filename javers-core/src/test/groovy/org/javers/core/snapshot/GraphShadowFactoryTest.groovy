package org.javers.core.snapshot

import org.javers.core.JaversTestBuilder
import org.javers.core.model.SnapshotEntity
import org.javers.core.graph.NodeAssert
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId

/**
 * @author bartosz walacik
 */
class GraphShadowFactoryTest extends Specification {

    @Shared JaversTestBuilder javers

    def setup(){
        javers = JaversTestBuilder.javersTestAssembly()
    }


    def "should create ShadowGraph with snapshots of committed objects "() {
        given:
        def oldRef = new SnapshotEntity(id: 2, intProperty:2)
        javers.javers().commit("user",oldRef)
        def cdo = new SnapshotEntity(id: 1, entityRef: oldRef)
        def liveGraph = javers.createLiveGraph(cdo)

        when:
        def shadow = javers.graphShadowFactory.createLatestShadow(liveGraph)

        then:
        shadow.flatten().size() == 1
        NodeAssert.assertThat(shadow.flatten()[0]).hasGlobalId(instanceId(2,SnapshotEntity))
                                                  .isSnapshot()
    }
}
