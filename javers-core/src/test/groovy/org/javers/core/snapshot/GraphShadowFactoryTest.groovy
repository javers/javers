package org.javers.core.snapshot

import org.javers.core.model.SnapshotEntity
import org.javers.test.assertion.NodeAssert
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
class GraphShadowFactoryTest extends Specification {

    def "should create one node Shadow with primitive property"() {
        given:
        def javers = javersTestAssembly()
        def cdo = new SnapshotEntity(id: 1, intProperty: 5)
        javers.javers().commit("user",cdo)

        when:
        def shadow = javers.graphShadowFactory.createLatestShadow(cdo)

        then:
        NodeAssert.assertThat(shadow).hasInstanceId(SnapshotEntity,1).hasNoEdges().isSnapshot()
        shadow.getCdo().getPropertyValue("intProperty") == 5
    }

    def "should create two node Shadow with SingleEdge"() {
        given:
        def javers = javersTestAssembly()
        def cdo = new SnapshotEntity(id: 1, entityRef: new SnapshotEntity(id: 2))
        javers.javers().commit("user",cdo)

        when:
        def shadow = javers.graphShadowFactory.createLatestShadow(cdo)

        then:
        NodeAssert.assertThat(shadow)
                  .isSnapshot()
                  .hasInstanceId(SnapshotEntity,1)
                  .hasSingleEdge("entityRef")
                  .andTargetNode()
                  .hasInstanceId(SnapshotEntity,2)
                  .isSnapshot()

    }

}
