package org.javers.core

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.SnapshotEntity
import org.javers.core.snapshot.SnapshotsAssert
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.JaversBuilder.javers
import static org.javers.repository.jql.InstanceIdDTO.instanceId
import static org.javers.repository.jql.ValueObjectIdDTO.valueObjectId
import static org.javers.test.builder.DummyUserBuilder.dummyUser

class JaversRepositoryE2ETest extends Specification {

    Javers javers

    def setup() {
        // InMemoryRepository is used by default
        javers = javers().build()
    }

    def "should find snapshots by changed property"() {
        given:
        def entity = new SnapshotEntity(id:1, intProperty: 4)
        javers.commit("author", entity)

        entity.dob = new LocalDate()
        javers.commit("author", entity)

        entity.intProperty = 5
        javers.commit("author", entity)

        when:
        def snapshots = javers.get

        then:
        false
    }

    def "should fetch terminal snapshots from the repository"() {
        given:
        def anEntity = new SnapshotEntity(id:1, entityRef: new SnapshotEntity(id:2))
        javers.commit("author", anEntity)
        javers.commitShallowDelete("author", anEntity)

        when:
        def snapshots = javers.getStateHistory(instanceId(1, SnapshotEntity), 10)

        then:
        SnapshotsAssert.assertThat(snapshots)
                       .hasSize(2)
                       .hasOrdinarySnapshot(instanceId(1,SnapshotEntity))
                       .hasTerminalSnapshot(instanceId(1,SnapshotEntity), "2.0")

    }

    def "should store state history of Entity in JaversRepository and fetch snapshots in reverse order"() {
        given:
        def ref = new SnapshotEntity(id:2)
        def cdo = new SnapshotEntity(id: 1,
                                     entityRef: ref,
                                     arrayOfIntegers: [1,2],
                                     listOfDates: [new LocalDate(2001,1,1), new LocalDate(2001,1,2)],
                                     mapOfValues: [(new LocalDate(2001,1,1)):1.1])
        javers.commit("author", cdo) //v. 1
        cdo.intProperty = 5
        javers.commit("author2", cdo) //v. 2

        when:
        def snapshots = javers.getStateHistory(instanceId(1, SnapshotEntity), 10)

        then:
        def cdoId = instanceId(1,SnapshotEntity)
        def refId = instanceId(2,SnapshotEntity)

        //assert properties
        SnapshotsAssert.assertThat(snapshots)
                .hasSnapshot(cdoId, "2.0", [id:1,
                                            entityRef:refId,
                                            arrayOfIntegers:[1,2],
                                            listOfDates: [new LocalDate(2001,1,1), new LocalDate(2001,1,2)],
                                            mapOfValues: [(new LocalDate(2001,1,1)):1.1],
                                            intProperty:5,])
        //assert metadata
        with(snapshots[0]) {
             commitId == "2.0"
             commitMetadata.author == "author2"
             commitMetadata.commitDate
             !initial
        }
        with(snapshots[1]) {
            commitId == "1.0"
            commitMetadata.author == "author"
            commitMetadata.commitDate
            !getPropertyValue("intProperty")
            initial
        }
    }

    def "should compare Entity property values with latest from repository"() {
        given:
        def user = dummyUser("John").withAge(18).build()
        javers.commit("login", user)

        when:
        user.age = 19
        javers.commit("login", user)
        def history = javers.getChangeHistory(instanceId("John", DummyUser), 100)

        then:
        with(history[0]) {
            it instanceof ValueChange
            affectedGlobalId == instanceId("John", DummyUser)
            property.name == "age"
            left == 18
            right == 19
        }
        history[1] instanceof NewObject
    }

    def "should compare ValueObject property values with latest from repository"() {
        given:
        def cdo = new SnapshotEntity(id: 1, listOfValueObjects: [new DummyAddress("London","street")])
        javers.commit("login", cdo)

        when:
        cdo.listOfValueObjects[0].city = "Paris"
        javers.commit("login", cdo)
        def voId = valueObjectId(1, SnapshotEntity, "listOfValueObjects/0")
        def history = javers.getChangeHistory(voId, 100)

        then:
        history.size() == 2
        with(history[0]) {
            it instanceof ValueChange
            affectedGlobalId == voId
            property.name == "city"
            left == "London"
            right == "Paris"
        }
        history[1] instanceof NewObject
    }

    @Unroll
    def "should store snapshot of #what and find it by id"() {
        given:
        def cdo = new SnapshotEntity(id: 1, listOfValueObjects: [new DummyAddress("London")])
        javers.commit("login", cdo)

        when:
        def snapshot = javers.getLatestSnapshot(givenId).get()

        then:
        snapshot.globalId == givenId
        snapshot.getPropertyValue(property) == expextedState

        where:
        what <<    ["Entity instance", "ValueObject"]
        givenId << [instanceId(1, SnapshotEntity), valueObjectId(1, SnapshotEntity, "listOfValueObjects/0")]
        property << ["id","city"]
        expextedState << [1,"London"]
     }
}