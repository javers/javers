package org.javers.core.snapshot

import org.javers.common.collections.Multimap
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.json.builder.GlobalCdoIdTestBuilder.*
import static org.javers.core.snapshot.SnapshotsAssert.getAssertThat

/**
 * @author bartosz walacik
 */
class GraphSnapshotFactoryTest extends Specification {
    def "should flatten straight Entity relation"() {
        given:
        def javers = javersTestAssembly()
        def cdo = new SnapshotEntity(id: 1, entityRef: new SnapshotEntity(id: 5))
        def node = javers.createObjectGraphBuilder().buildGraph(cdo)

        when:
        Multimap snapshots = javers.graphSnapshotFactory.create(node)

        then:
        assertThat(snapshots).hasSize(2)
                       .hasSnapshot(instanceId(1, SnapshotEntity))
                       .hasSnapshot(instanceId(5, SnapshotEntity))
    }

    def "should flatten straight ValueObject relation"() {
        given:
        def javers = javersTestAssembly()
        def cdo  = new SnapshotEntity(id:1, valueObjectRef: new DummyAddress("street"))
        def node = javers.createObjectGraphBuilder().buildGraph(cdo)

        when:
        Multimap snapshots = javers.graphSnapshotFactory.create(node)

        then:
        assertThat(snapshots).hasSize(2)
                       .hasSnapshot(instanceId(1, SnapshotEntity))
                       .hasSnapshot(valueObjectId(instanceId(1, SnapshotEntity),DummyAddress,"valueObjectRef"))
    }

    def "should flatten Set of ValueObject"() {
        def javers = javersTestAssembly()
        def cdo = new SnapshotEntity(setOfValueObjects: [new DummyAddress("London"), new DummyAddress("London City")])
        def node = javers.createObjectGraphBuilder().buildGraph(cdo)

        when:
        Multimap snapshots = javers.graphSnapshotFactory.create(node)

        then:
        assertThat(snapshots).hasSize(3)
                             .hasSnapshot(instanceId(1, SnapshotEntity))
                             .hasSnapshotSet(valueObjectSetId(instanceId(1, SnapshotEntity),DummyAddress,"setOfValueObjects"),2)

    }

    @Unroll
    def "should flatten #listType of ValueObject"() {
        given:
        def javers = javersTestAssembly()
        def node = javers.createObjectGraphBuilder().buildGraph(cdo)

        when:
        Multimap snapshots = javers.graphSnapshotFactory.create(node)

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
                    [valueObjectId(instanceId(1, SnapshotEntity),DummyAddress,"listOfValueObjects/0"),
                     valueObjectId(instanceId(1, SnapshotEntity),DummyAddress,"listOfValueObjects/1")],
                    [valueObjectId(instanceId(1, SnapshotEntity),DummyAddress,"arrayOfValueObjects/0"),
                     valueObjectId(instanceId(1, SnapshotEntity),DummyAddress,"arrayOfValueObjects/1")]
                    ]
    }

    @Unroll
    def "should flatten #containerType of Entity"() {
        given:
        def javers = javersTestAssembly()
        def node = javers.createObjectGraphBuilder().buildGraph(cdo)

        when:
        Multimap snapshots = javers.graphSnapshotFactory.create(node)

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
}
