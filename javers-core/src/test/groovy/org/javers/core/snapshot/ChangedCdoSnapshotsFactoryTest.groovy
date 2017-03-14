package org.javers.core.snapshot

import org.javers.core.JaversTestBuilder
import org.javers.core.commit.CommitId
import org.javers.core.commit.CommitMetadata
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import java.time.LocalDateTime
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.repository.jql.InstanceIdDTO.instanceId
import static org.javers.repository.jql.ValueObjectIdDTO.valueObjectId
import static org.javers.core.snapshot.SnapshotsAssert.getAssertThat

/**
 * @author bartosz walacik
 */
class ChangedCdoSnapshotsFactoryTest extends Specification {

    @Shared JaversTestBuilder javers
    @Shared ChangedCdoSnapshotsFactory changedCdoSnapshotsFactory

    def setup(){
        javers = JaversTestBuilder.javersTestAssembly()
        changedCdoSnapshotsFactory = javers.getContainerComponent(ChangedCdoSnapshotsFactory)
    }

    CommitMetadata someCommitMetadata(){
        new CommitMetadata("kazik", [:], LocalDateTime.now(), new CommitId(1, 0))
    }

    def "should not mark snapshot as initial even if not present in previous commit but committed before"() {
        given:
        def cdo5 = new SnapshotEntity(id: 5)
        javers.javers().commit("author", cdo5)

        def cdo1 = new SnapshotEntity(id: 1)
        javers.javers().commit("author", cdo1)

        when:
        cdo1.entityRef = cdo5
        cdo5.intProperty = 1
        def commit = javers.javers().commit("author", cdo1)

        then:
        assertThat(commit.snapshots)
                .hasSize(2)
                .hasOrdinarySnapshot(instanceId(5, SnapshotEntity))
                .hasOrdinarySnapshot(instanceId(1, SnapshotEntity))
    }

    def "should mark first snapshot as initial"() {
        given:
        def cdo = new SnapshotEntity(id: 1)

        when:
        def commit = javers.javers().commit("author", cdo)

        then:
        assertThat(commit.snapshots)
                .hasSize(1)
                .hasInitialSnapshot(instanceId(1, SnapshotEntity))

        when:
        cdo.entityRef = new SnapshotEntity(id: 5)
        commit = javers.javers().commit("author", cdo)

        then:
        assertThat(commit.snapshots)
                .hasSize(2)
                .hasInitialSnapshot(instanceId(5, SnapshotEntity))
                .hasOrdinarySnapshot(instanceId(1, SnapshotEntity))
    }

    def "should flatten straight Entity relation"() {
        given:
        def cdo = new SnapshotEntity(id: 1, entityRef: new SnapshotEntity(id: 5))
        def node = javers.createLiveGraph(cdo)

        when:
        def snapshots = changedCdoSnapshotsFactory.create(node.cdos(), [] as Set, someCommitMetadata())

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
        def snapshots = changedCdoSnapshotsFactory.create(node.cdos(), [] as Set, someCommitMetadata())

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
        def snapshots = changedCdoSnapshotsFactory.create(node.cdos(), [] as Set, someCommitMetadata())

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
        def snapshots = changedCdoSnapshotsFactory.create(node.cdos(), [] as Set, someCommitMetadata())

        then:
        assertThat(snapshots)
                .hasSize(3)
                .hasSnapshot(instanceId(1, SnapshotEntity))
                .hasSnapshot(valueObjectId(1, SnapshotEntity, "setOfValueObjects/"+javers.addressHash("London")))
                .hasSnapshot(valueObjectId(1, SnapshotEntity, "setOfValueObjects/"+javers.addressHash("London City")))
    }

    @Unroll
    def "should flatten #listType of ValueObject"() {
        given:
        def node = javers.createLiveGraph(cdo)

        when:
        def snapshots = changedCdoSnapshotsFactory.create(node.cdos(), [] as Set, someCommitMetadata())

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
        def snapshots = changedCdoSnapshotsFactory.create(node.cdos(), [] as Set, someCommitMetadata())

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
        def snapshots = changedCdoSnapshotsFactory.create(node.cdos(), [] as Set, someCommitMetadata())

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
        def cdo = new SnapshotEntity(listOfEntities: [new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def firstCommit = javers.javers().commit("author",cdo)

        when:
        def secondCommit = javers.javers().commit("author",cdo)

        then:
        firstCommit.snapshots.size() == 3
        secondCommit.snapshots.size() == 0
        secondCommit.changes.size() == 0
    }

    def "should reuse existing root snapshot when not changed"() {
        given:
        def cdo = new SnapshotEntity(listOfEntities: [new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def firstCommit = javers.commitFactory.create("author",[:],cdo)
        javers.javersRepository.persist(firstCommit)

        when:
        cdo.listOfEntities.get(0).intProperty = 1
        cdo.listOfEntities.get(1).intProperty = 1
        def secondCommit = javers.javers().commit("author",cdo)

        then:
        assertThat(secondCommit.snapshots)
                .hasSize(2)
                .hasSnapshot(instanceId(2, SnapshotEntity))
                .hasSnapshot(instanceId(3, SnapshotEntity))
    }

    def "should reuse existing ref snapshots when not changed"() {
        given:
        def cdo = new SnapshotEntity(listOfEntities: [new SnapshotEntity(id:2), new SnapshotEntity(id:3)])
        def firstCommit = javers.commitFactory.create("author",[:],cdo)
        javers.javersRepository.persist(firstCommit)

        when:
        cdo.intProperty = 1
        def secondCommit = javers.javers().commit("author",cdo)

        then:
        assertThat(secondCommit.snapshots)
                .hasSize(1)
                .hasSnapshot(instanceId(1, SnapshotEntity))
    }

}
