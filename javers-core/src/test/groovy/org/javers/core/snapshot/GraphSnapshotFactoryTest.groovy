package org.javers.core.snapshot

import org.javers.common.collections.Multimap
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.model.SnapshotEntity
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.json.builder.GlobalCdoIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class GraphSnapshotFactoryTest extends Specification {
    def "should flatten many to one Entity relation"() {
        given:
        def javers = javersTestAssembly()
        def cdo  = new SnapshotEntity(id:1, entityRef:new SnapshotEntity(id:5))
        def node = javers.createObjectGraphBuilder().buildGraph(cdo)

        when:
        Multimap snapshots = javers.graphSnapshotFactory.create(node)

        then:
        snapshots.size() == 2
        CdoSnapshot s1 = snapshots.getOne(instanceId(1, SnapshotEntity))
        CdoSnapshot s5 = snapshots.getOne(instanceId(5, SnapshotEntity))
        s1.globalId == instanceId(1, SnapshotEntity)
        s1.getPropertyValue("entityRef") == instanceId(5, SnapshotEntity)
        s5.globalId == instanceId(5, SnapshotEntity)
    }


  //  propertyType << ["Entity","ValueObject"]
 //   propertyName << ["entityRef", "valueObjectRef"]
 //   cdo <<          [new SnapshotEntity(id:1, entityRef:new SnapshotEntity(id:5)),
  //  new SnapshotEntity(id:1, valueObjectRef: new DummyAddress("street"))]
  //  expectedVal <<  [instanceId(5, SnapshotEntity),
  //  valueObjectId(instanceId(1, SnapshotEntity),DummyAddress,"valueObjectRef")]

}
