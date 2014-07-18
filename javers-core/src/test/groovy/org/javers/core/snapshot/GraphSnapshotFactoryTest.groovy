package org.javers.core.snapshot

import org.javers.core.JaversTestBuilder
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.metamodel.object.InstanceId.InstanceIdDTO.instanceId
import static org.javers.core.metamodel.object.ValueObjectId.ValueObjectIdDTO.valueObjectId
import static org.javers.core.snapshot.SnapshotsAssert.getAssertThat

/**
 * @author bartosz walacik
 */
class GraphSnapshotFactoryTest extends Specification {

    @Shared JaversTestBuilder javers

    def setup(){
        javers = JaversTestBuilder.javersTestAssembly()
    }

    def "should flatten straight Entity relation"() {
        given:
        def cdo = new SnapshotEntity(id: 1, entityRef: new SnapshotEntity(id: 5))
        def node = javers.createLiveGraph(cdo)

        when:
        List snapshots = javers.graphSnapshotFactory.create(node, "author")

        then:
        assertThat(snapshots).hasSize(2)
                             .hasSnapshot(instanceId(1, SnapshotEntity))
                             .hasSnapshot(instanceId(5, SnapshotEntity))
    }

    def "should flatten graph with depth 2"(){
        given:
        def ref3  = new SnapshotEntity(id:3)
        def ref2  = new SnapshotEntity(id:2,entityRef: ref3)
        //cdo -> ref2 -> ref3
        def cdo   = new SnapshotEntity(id:1,entityRef: ref2)
        def node = javers.createLiveGraph(cdo)

        when:
        List snapshots = javers.graphSnapshotFactory.create(node, "author")

        then:
        assertThat(snapshots).hasSize(3)
                             .hasSnapshot(instanceId(1, SnapshotEntity))
                             .hasSnapshot(instanceId(2, SnapshotEntity))
                             .hasSnapshot(instanceId(3, SnapshotEntity))
    }

    def "should flatten straight ValueObject relation"() {
        given:
        def cdo  = new SnapshotEntity(id:1, valueObjectRef: new DummyAddress("street"))
        def node = javers.createLiveGraph(cdo)

        when:
        List snapshots = javers.graphSnapshotFactory.create(node, "author")

        then:
        assertThat(snapshots).hasSize(2)
                             .hasSnapshot(instanceId(1, SnapshotEntity))
                             .hasSnapshot(valueObjectId(1, SnapshotEntity,"valueObjectRef"))
    }

    def "should flatten Set of ValueObject"() {
        given:
        def cdo = new SnapshotEntity(setOfValueObjects: [new DummyAddress("London"), new DummyAddress("London City")])
        def node = javers.createLiveGraph(cdo)

        when:
        List snapshots = javers.graphSnapshotFactory.create(node, "author")

        then:
        assertThat(snapshots).hasSize(3)
                             .hasSnapshot(instanceId(1, SnapshotEntity))
                             .hasSnapshot(valueObjectId(1, SnapshotEntity,"setOfValueObjects/random_0"))
                             .hasSnapshot(valueObjectId(1, SnapshotEntity,"setOfValueObjects/random_1"))

    }

    @Unroll
    def "should flatten #listType of ValueObject"() {
        given:
        def node = javers.createLiveGraph(cdo)

        when:
        List snapshots = javers.graphSnapshotFactory.create(node, "author")

        then:
        assertThat(snapshots).hasSize(3)
                             .hasSnapshot(instanceId(1, SnapshotEntity))
                             .hasSnapshot(expectedVoIds[0])
                             .hasSnapshot(expectedVoIds[1])

        where:
        listType << ["List", "Array"]
        cdo <<      [new SnapshotEntity(listOfValueObjects:  [new DummyAddress("London"), new DummyAddress("London City")]),
                     new SnapshotEntity(arrayOfValueObjects: [new DummyAddress("London"), new DummyAddress("London City")])
                    ]
        expectedVoIds << [
                    [valueObjectId(1, SnapshotEntity,"listOfValueObjects/0"),
                     valueObjectId(1, SnapshotEntity,"listOfValueObjects/1")],
                    [valueObjectId(1, SnapshotEntity,"arrayOfValueObjects/0"),
                     valueObjectId(1, SnapshotEntity,"arrayOfValueObjects/1")]
                    ]

    }

    @Unroll
    def "should flatten #containerType of Entity"() {
        given:
        def node = javers.createLiveGraph(cdo)

        when:
        List snapshots = javers.graphSnapshotFactory.create(node, "author")

        then:
        assertThat(snapshots).hasSize(3)
                             .hasSnapshot(instanceId(1, SnapshotEntity))
                             .hasSnapshot(instanceId(5, SnapshotEntity))
                             .hasSnapshot(instanceId(6, SnapshotEntity))

        where:
        containerType << ["List", "Array", "Set"]
        cdo <<      [new SnapshotEntity(listOfEntities:  [new SnapshotEntity(id:5), new SnapshotEntity(id:6)]),
                     new SnapshotEntity(arrayOfEntities: [new SnapshotEntity(id:5), new SnapshotEntity(id:6)]),
                     new SnapshotEntity(setOfEntities:   [new SnapshotEntity(id:5), new SnapshotEntity(id:6)])
                    ]
    }

    @Unroll
    def "should flatten Map of <#keyType, #valueType>"() {
        given:
        def node = javers.createLiveGraph(cdo)

        when:
        List snapshots = javers.graphSnapshotFactory.create(node, "author")

        then:
        assertThat(snapshots).hasSize(3)
                             .hasSnapshot(instanceId(1, SnapshotEntity))
                             .hasSnapshot(expectedVoIds[0])
                             .hasSnapshot(expectedVoIds[1])

        where:
        keyType <<   ["Entity", "Primitive"]
        valueType << ["Entity", "ValueObject"]
        propertyName <<  ["mapOfEntities","mapPrimitiveToVO"]
        cdo << [
                new SnapshotEntity(mapOfEntities:    [(new SnapshotEntity(id:2)): new SnapshotEntity(id:3)]),
                new SnapshotEntity(mapPrimitiveToVO: ["key1": new DummyAddress("London"), "key2": new DummyAddress("City")])
        ]
        expectedVoIds << [ [instanceId(2, SnapshotEntity),instanceId(3, SnapshotEntity)],
                           [valueObjectId(1, SnapshotEntity,"mapPrimitiveToVO/key1"),
                            valueObjectId(1, SnapshotEntity,"mapPrimitiveToVO/key2")]
                         ]
    }

    def "should reuse existing snapshots when nothing changed"() {
        given:
        def cdo = new SnapshotEntity(listOfEntities:    [new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def firstCommit = javers.commitFactory.create("author",cdo)
        javers.javersRepository.persist(firstCommit)

        when:
        def secondSnapshots = javers.graphSnapshotFactory.create(javers.createLiveGraph(cdo), "author")

        then:
        firstCommit.snapshots.size() == 3
        !secondSnapshots
    }

    def "should reuse existing root snapshot when not changed"() {
        given:
        def cdo = new SnapshotEntity(listOfEntities: [new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def firstCommit = javers.commitFactory.create("author",cdo)
        javers.javersRepository.persist(firstCommit)

        when:
        cdo.listOfEntities.get(0).intProperty = 1
        cdo.listOfEntities.get(1).intProperty = 1
        def secondSnapshots = javers.graphSnapshotFactory.create(javers.createLiveGraph(cdo), "author")

        then:
        assertThat(secondSnapshots).hasSize(2)
                                   .hasSnapshot(instanceId(2, SnapshotEntity))
                                   .hasSnapshot(instanceId(3, SnapshotEntity))
    }

    def "should reuse existing ref snapshots when not changed"() {
        given:
        def cdo = new SnapshotEntity(listOfEntities: [new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def firstCommit = javers.commitFactory.create("author",cdo)
        javers.javersRepository.persist(firstCommit)

        when:
        cdo.intProperty = 1
        def secondSnapshots = javers.graphSnapshotFactory.create(javers.createLiveGraph(cdo), "author")

        then:
        assertThat(secondSnapshots).hasSize(1)
                                   .hasSnapshot(instanceId(1, SnapshotEntity))
    }

}
