package org.javers.core.snapshot

import org.javers.core.JaversTestBuilder
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import org.javers.test.assertion.NodeAssert
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author bartosz walacik
 */
class GraphShadowFactoryTest extends Specification {

    @Shared JaversTestBuilder javers

    def setup(){
        javers = JaversTestBuilder.javersTestAssembly()
    }

    def "should create one node Shadow with primitive property"() {
        given:
        def cdo = new SnapshotEntity(id: 1, intProperty: 5)
        javers.javers().commit("user",cdo)

        when:
        def shadow = javers.graphShadowFactory.createLatestShadow(cdo).get()

        then:
        NodeAssert.assertThat(shadow).hasInstanceId(SnapshotEntity,1).hasNoEdges().isSnapshot()
        shadow.getCdo().getPropertyValue("intProperty") == 5
    }

    @Unroll
    def "should create 2 node Shadow with SingleEdge to #propertyType"() {
        given:
        javers.javers().commit("user",cdo)

        when:
        def shadow = javers.graphShadowFactory.createLatestShadow(cdo).get()

        then:
        NodeAssert.assertThat(shadow)
                  .isSnapshot()
                  .hasInstanceId(SnapshotEntity,1)
                  .hasSingleEdge(propertyName)
                  .andTargetNode()
                  .hasGlobalId(expectedRefId)
                  .isSnapshot()

        where:
        propertyType << ["Entity","ValueObject"]
        propertyName << ["entityRef", "valueObjectRef"]
        cdo <<           [new SnapshotEntity(id:1, entityRef:new SnapshotEntity(id:5)),
                          new SnapshotEntity(id:1, valueObjectRef: new DummyAddress("London"))]
        expectedRefId << [javers.instanceId(5, SnapshotEntity),
                          javers.voBuilder(1, SnapshotEntity).voId(DummyAddress,"valueObjectRef")]

    }

    @Unroll
    def "should create 3 node Shadow with MultiEdge to #propertyType"() {
        given:
        javers.javers().commit("user",cdo)

        when:
        def shadow = javers.graphShadowFactory.createLatestShadow(cdo).get()

        then:
        NodeAssert.assertThat(shadow)
                  .isSnapshot()
                  .hasInstanceId(SnapshotEntity,1)
                  .hasMultiEdge(propertyName)
                  .ofSize(2)
                  .refersToGlobalIds(expectedRefIds)
                  .refersToSnapshots()

        where:
        propertyType << ["Entity","ValueObject"]
        propertyName << ["listOfEntities", "listOfValueObjects"]
        cdo <<          [new SnapshotEntity(listOfEntities:      [new SnapshotEntity(id:2), new SnapshotEntity(id:3)]),
                         new SnapshotEntity(listOfValueObjects:  [new DummyAddress("London"), new DummyAddress("London City")])
                        ]
        expectedRefIds << [
                           [javers.instanceId(2, SnapshotEntity), javers.instanceId(3, SnapshotEntity)],
                           [javers.voBuilder(1, SnapshotEntity).voId(DummyAddress,"listOfValueObjects/0"),
                            javers.voBuilder(1, SnapshotEntity).voId(DummyAddress,"listOfValueObjects/1")]
                          ]
    }

    def "should return ObjectNode.EMPTY when root is new and ref is new"() {
        given:
        def cdo = new SnapshotEntity(id: 1, entityRef: new SnapshotEntity(id:2))

        when:
        def shadow = javers.graphShadowFactory.createLatestShadow(cdo)

        then:
        shadow.empty
    }
}
