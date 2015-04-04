package org.javers.core

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.SnapshotEntity
import org.javers.core.snapshot.SnapshotsAssert
import org.javers.repository.jql.QueryBuilder
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

    def "should find snapshots and changes by Entity class and changed property"() {
        given:
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 1))
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 1, dob: new LocalDate()))
        javers.commit("author", new DummyAddress())
        javers.commit("author", new SnapshotEntity(id:2, intProperty: 1))
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 2))

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(SnapshotEntity).andProperty("intProperty").build())

        then:
        snapshots.size() == 3
        snapshots[0].commitId.majorId == 5
        snapshots.each {
            assert it.globalId.cdoClass.clientsClass == SnapshotEntity
        }

        when:
        def changes = javers.findChanges(QueryBuilder.byClass(SnapshotEntity).andProperty("intProperty").build())

        then:
        changes.size() == 3
        changes[0].getCommitMetadata().get().id.majorId == 5
        changes.each {
            assert it instanceof ValueChange
            assert it.propertyName == "intProperty"
        }
    }

    def "should find Entity changes by Entity class"() {
        given:
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 1))
        javers.commit("author", new SnapshotEntity(id:2, intProperty: 1))
        javers.commit("author", new DummyAddress())
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 2))
        javers.commit("author", new SnapshotEntity(id:2, intProperty: 2))

        when:
        def changes = javers.findChanges(QueryBuilder.byClass(SnapshotEntity).build())

        then:
        changes.size() == 8
        changes[0].commitMetadata.get().id.majorId == 5
        changes.findAll{it instanceof ValueChange}.size() == 6
        changes.findAll{it instanceof NewObject}.size() == 2
    }

    def "should find Entity snapshots by Entity class"() {
         given:
         javers.commit("author", new SnapshotEntity(id:1))
         javers.commit("author", new SnapshotEntity(id:2))
         javers.commit("author", new DummyAddress())

         when:
         def snapshots = javers.findSnapshots(QueryBuilder.byClass(SnapshotEntity).build())

         then:
         snapshots.size() == 2
         snapshots[0].commitId.majorId == 2
         snapshots.each {
             assert it.globalId.cdoClass.clientsClass == SnapshotEntity
         }
    }

    def "should find ValueObject snapshots by ValueObject class"() {
        given:
        javers.commit("author", new DummyAddress(city:"London"))
        javers.commit("author", new DummyAddress(city:"Paris"))
        javers.commit("author", new SnapshotEntity(id:2))

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(DummyAddress).build())

        then:
        snapshots.size() == 2
        snapshots.each {
            assert it.globalId.cdoClass.clientsClass == DummyAddress
        }
    }

    def "should find snapshots and changes by Id and changed property"() {
        given:
        def entity = new SnapshotEntity(id:1, intProperty: 4)
        javers.commit("author", entity)

        entity.dob = new LocalDate()
        javers.commit("author", entity)

        entity.intProperty = 5
        javers.commit("author", entity)

        when: "should find snapshots"
        def snapshots = javers.findSnapshots(
                QueryBuilder.byInstanceId(1, SnapshotEntity).andProperty("intProperty").build())

        then:
        snapshots.size() == 2
        snapshots[0].commitId.majorId == 3
        snapshots[1].commitId.majorId == 1

        when: "should find changes"
        def changes = javers.findChanges(
                QueryBuilder.byInstanceId(1, SnapshotEntity).andProperty("intProperty").build())

        then:
        changes.size() == 2
        changes[0].commitMetadata.get().id.majorId == 3
        changes[0].left == 4
        changes[0].right == 5
        changes[1].commitMetadata.get().id.majorId == 1
        changes[1].left == 0
        changes[1].right == 4
        changes.each {
            assert it instanceof ValueChange
            assert it.propertyName == "intProperty"
        }
    }

    def "should fetch terminal snapshots from the repository"() {
        given:
        def anEntity = new SnapshotEntity(id:1, entityRef: new SnapshotEntity(id:2))
        javers.commit("author", anEntity)
        javers.commitShallowDelete("author", anEntity)

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())

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
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())

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
             commitId.value() == "2.0"
             commitMetadata.author == "author2"
             commitMetadata.commitDate
             !initial
        }
        with(snapshots[1]) {
            commitId.value() == "1.0"
            commitMetadata.author == "author"
            commitMetadata.commitDate
            !getPropertyValue("intProperty")
            initial
        }
    }

    def "should compare Entity properties with latest from repository"() {
        given:
        def user = dummyUser("John").withAge(18).build()
        javers.commit("login", user)

        when:
        user.age = 19
        javers.commit("login", user)
        def history = javers.findChanges(QueryBuilder.byInstanceId("John", DummyUser).build())

        then:
        with(history[0]) {
            it instanceof ValueChange
            affectedGlobalId == instanceId("John", DummyUser)
            propertyName == "age"
            left == 18
            right == 19
        }
    }

    def "should compare ValueObject properties with latest from repository"() {
        given:
        def cdo = new SnapshotEntity(id: 1, listOfValueObjects: [new DummyAddress("London","street")])
        javers.commit("login", cdo)

        when:
        cdo.listOfValueObjects[0].city = "Paris"
        javers.commit("login", cdo)
        def history = javers.findChanges(
                QueryBuilder.byValueObjectId(1, SnapshotEntity, "listOfValueObjects/0").build())


        then:
        with(history[0]) {
            it instanceof ValueChange
            affectedGlobalId == valueObjectId(1, SnapshotEntity, "listOfValueObjects/0")
            propertyName == "city"
            left == "London"
            right == "Paris"
        }
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