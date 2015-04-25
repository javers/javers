package org.javers.core

import org.javers.core.diff.changetype.ValueChange
import org.javers.core.model.*
import org.javers.core.snapshot.SnapshotsAssert
import org.javers.repository.jql.QueryBuilder
import org.joda.time.LocalDate
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.JaversBuilder.javers
import static org.javers.repository.jql.InstanceIdDTO.instanceId
import static org.javers.repository.jql.UnboundedValueObjectIdDTO.unboundedValueObjectId
import static org.javers.repository.jql.ValueObjectIdDTO.valueObjectId
import static org.javers.test.builder.DummyUserBuilder.dummyUser

class JaversRepositoryE2ETest extends Specification {

    Javers javers

    def setup() {
        // InMemoryRepository is used by default
        javers = javers().build()
    }

    def "should query for ValueObject changes by owning Entity class"() {
        given:
        def data = [ new DummyUserDetails(id:1, dummyAddress: new DummyAddress(city:"London")),
                     new DummyUserDetails(id:1, dummyAddress: new DummyAddress(city:"Paris")),
                     new SnapshotEntity(id:1, valueObjectRef: new DummyAddress(city:"London")),
                     new SnapshotEntity(id:1, valueObjectRef: new DummyAddress(city:"Paris")),
                     new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city:"Rome")),
                     new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city:"Paris")),
                     //noise
                     new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city:"Paris"), arrayOfValueObjects: [new DummyAddress(city:"Luton")])
        ]

        data.each{
            javers.commit("author",it)
        }

        when:
        def changes = javers.findChanges(QueryBuilder.byValueObject(SnapshotEntity, "valueObjectRef").build())

        then:
        changes.size() == 2
        changes[0].commitMetadata.get().id.majorId == 6
        changes.each{
            assert it.affectedGlobalId.fragment == "valueObjectRef"
            assert it.affectedGlobalId.cdoClass.getClientsClass() == DummyAddress
        }
    }

    def "should query for ValueObject changes by (owning Entity) GlobalId"() {
        given:
        def vo = new DummyAddress(city: "London")
        def entity = new SnapshotEntity(id:1, valueObjectRef: vo)
        javers.commit("author", entity)
        javers.commit("author", new DummyUserDetails(id:1, dummyAddress: new DummyAddress(city: "Paris"))) //noise

        vo.city = "Paris"
        javers.commit("author", entity)

        when:
        def changes = javers.findChanges(QueryBuilder.byValueObjectId(1,SnapshotEntity,"valueObjectRef").build())

        then:
        changes.size() == 1
        changes[0].commitMetadata.get().id.majorId == 3
        changes.each {
            assert it.affectedGlobalId == valueObjectId(1,SnapshotEntity,"valueObjectRef")
        }
    }

    @Unroll
    def "should query for #what snapshot by GlobalId with limit"() {
        given:
        objects.each {
            javers.commit("author",it)
        }

        when:
        def snapshots = javers.findSnapshots(query)

        then:
        snapshots.size() == 3
        snapshots[0].commitId.majorId == 5
        snapshots.each {
            assert it.globalId == expectedGlobalId
        }

        where:
        what <<    ["Entity", "Unbounded ValueObject", "Bounded ValueObject"]
        objects << [
                    (1..5).collect{ new SnapshotEntity(id:1,intProperty: it) }
                     + new SnapshotEntity(id:2), //noise
                    (1..5).collect{ new DummyAddress(city: "London"+it)}
                     + new DummyPoint(1,2), //noise
                    (1..5).collect{ new SnapshotEntity(id:1,valueObjectRef: new DummyAddress(city: "London"+it)) }
                     + new SnapshotEntity(id:2,valueObjectRef: new DummyAddress(city: "London"+1)) //noise
                   ]
        query   << [QueryBuilder.byInstanceId(1, SnapshotEntity).limit(3).build(),
                    QueryBuilder.byClass(DummyAddress).limit(3).build(),
                    QueryBuilder.byValueObjectId(1,SnapshotEntity,"valueObjectRef").limit(3).build()
                   ]
        expectedGlobalId << [instanceId(1,SnapshotEntity),
                             unboundedValueObjectId(DummyAddress),
                             valueObjectId(1,SnapshotEntity,"valueObjectRef")
                            ]
    }

    def "should query for ValueObject snapshots by ValueObject class and changed property"() {
        given:
        def objects = [
          new SnapshotEntity(id:1, valueObjectRef: new DummyAddress(city: "London",   street: "str")) ,
          new SnapshotEntity(id:1, valueObjectRef: new DummyAddress(city: "London 2", street: "str")) ,
          new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city: "Paris", street: "str")) ,
          new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city: "Paris", street: "str 2"))] //noise
        objects.each {
            javers.commit("author", it)
        }

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(DummyAddress).andProperty("city").build())

        then:
        snapshots.size() == 3
        snapshots.each {
            assert it.globalId.cdoClass.clientsClass == DummyAddress
        }
    }

    def "should query for Entity snapshots and changes by Entity class and changed property"() {
        given:
        javers.commit( "author", new SnapshotEntity(id:1, intProperty: 1) )
        javers.commit( "author", new SnapshotEntity(id:1, intProperty: 1, dob: new LocalDate()) ) //noise
        javers.commit( "author", new SnapshotEntity(id:1, intProperty: 2) )
        javers.commit( "author", new DummyAddress() ) //noise
        javers.commit( "author", new SnapshotEntity(id:2, intProperty: 1) )

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(SnapshotEntity).andProperty("intProperty").build())

        then: "snapshots query"
        snapshots.size() == 3
        snapshots[0].commitId.majorId == 5
        snapshots.each {
            assert it.globalId.cdoClass.clientsClass == SnapshotEntity
        }

        when: "changes query"
        def changes = javers.findChanges(QueryBuilder.byClass(SnapshotEntity).andProperty("intProperty").build())

        then:
        changes.size() == 1
        changes[0].getCommitMetadata().get().id.majorId == 3
        changes.each {
            assert it instanceof ValueChange
            assert it.propertyName == "intProperty"
        }
    }

    def "should query for Entity changes by Entity class"() {
        given:
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 1))
        javers.commit("author", new SnapshotEntity(id:2, intProperty: 1))
        javers.commit("author", new DummyAddress())
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 2))
        javers.commit("author", new SnapshotEntity(id:2, intProperty: 2))

        when:
        def changes = javers.findChanges(QueryBuilder.byClass(SnapshotEntity).build())

        then:
        changes.size() == 2
        changes[0].commitMetadata.get().id.majorId == 5
        changes.each {assert it instanceof ValueChange}
    }

    def "should query for Entity snapshots by Entity class"() {
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

    @Unroll
    def "should query for #voType ValueObject snapshots by ValueObject class"() {
        given:
        objects.each {
            javers.commit("author", it)
        }

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(DummyAddress).build())

        then:
        snapshots.size() == 2
        snapshots.each {
            assert it.globalId.cdoClass.clientsClass == DummyAddress
        }

        where:
        voType <<  ["Bounded","Unbounded"]
        objects << [[new SnapshotEntity(id:1, valueObjectRef: new DummyAddress(city: "London")),
                     new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city: "London"))],
                    [new DummyAddress(city:"London"), new DummyAddress(city:"Paris")]
                   ]
    }

    def "should query for Entity snapshots and changes by GlobalId and changed property"() {
        given:
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 4))
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 4, dob : new LocalDate()))
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 5, dob : new LocalDate()))
        javers.commit("author", new SnapshotEntity(id:2, intProperty: 4)) //noise

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
        changes.size() == 1
        changes[0] instanceof ValueChange
        changes[0].commitMetadata.get().id.majorId == 3
        changes[0].left == 4
        changes[0].right == 5
    }

    @Unroll
    def "should query for LatestSnapshot of #what by GlobalId"() {
        given:
        cdos.each{
            javers.commit("login", it)
        }

        when:
        def snapshot = javers.getLatestSnapshot(givenId).get()

        then:
        snapshot.globalId == givenId
        snapshot.commitId.majorId == 2
        snapshot.getPropertyValue(property) == expextedState

        where:
        cdos  <<  [[new SnapshotEntity(id: 1, intProperty: 1), new SnapshotEntity(id: 1, intProperty: 2)],
                   [new SnapshotEntity(id: 1, valueObjectRef: new DummyAddress("London")),
                    new SnapshotEntity(id: 1, valueObjectRef: new DummyAddress("Paris"))],
                   [new DummyAddress("London"), new DummyAddress("Paris")]]
        what <<    ["Entity", "Bounded ValueObject", "Unbounded ValueObject"]
        givenId << [instanceId(1, SnapshotEntity),
                    valueObjectId(1, SnapshotEntity, "valueObjectRef"),
                    unboundedValueObjectId(DummyAddress)]
        property <<      ["intProperty", "city",  "city"]
        expextedState << [2,             "Paris", "Paris"]
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

    def "should fetch changes in reverse chronological order"() {
        given:
        def user = new SnapshotEntity(id:1)
        def n = 25

        (1..n).each {
            user.intProperty = it
            javers.commit("some.login", user)
        }

        when:
        def changes = javers.findChanges(
            QueryBuilder.byInstanceId(1,SnapshotEntity).andProperty("intProperty").build())

        then:
        changes.size() == n-1
        (0..n-2).each {
            def change = changes[it]
            change.commitMetadata.get().id.majorId == n-it
            assert change.left  == n-it-1
            assert change.right == n-it
        }
    }

    def "should store state history of Entity in JaversRepository"() {
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
             changed.size() == 1
             changed[0].name == "intProperty"
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

}