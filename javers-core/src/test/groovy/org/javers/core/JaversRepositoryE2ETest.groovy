package org.javers.core

import org.javers.common.date.FakeDateProvider
import org.javers.common.reflection.ConcreteWithActualType
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.examples.typeNames.*
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.model.*
import org.javers.core.model.SnapshotEntity.DummyEnum
import org.javers.core.snapshot.SnapshotsAssert
import org.javers.repository.jql.QueryBuilder
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.JaversBuilder.javers
import static org.javers.repository.jql.InstanceIdDTO.instanceId
import static org.javers.repository.jql.QueryBuilder.byClass
import static org.javers.repository.jql.QueryBuilder.byInstanceId
import static org.javers.repository.jql.UnboundedValueObjectIdDTO.unboundedValueObjectId
import static org.javers.repository.jql.ValueObjectIdDTO.valueObjectId
import static org.javers.test.builder.DummyUserBuilder.dummyUser

class JaversRepositoryE2ETest extends Specification {
    FakeDateProvider fakeDateProvider
    Javers javers

    def setup() {
        JaversBuilder javersBuilder = configureJavers(javers())
        javers = javersBuilder.build()
    }

    JaversBuilder configureJavers(JaversBuilder javersBuilder) {
        // InMemoryRepository is used by default
        fakeDateProvider = new FakeDateProvider()
        javersBuilder.withDateTimeProvider(fakeDateProvider)
    }

    def "should support EmbeddedId as Entity Id"(){
      given:
      def javers = javers().build()
      def cdo  = new DummyEntityWithEmbeddedId(point: new DummyPoint(1,2), someVal: 5)

      when:
      javers.commit("author", cdo)

      then:
      def snapshot = javers.getLatestSnapshot(new DummyPoint(1,2), DummyEntityWithEmbeddedId).get()
      snapshot.globalId.cdoId == new DummyPoint(1,2)
    }

    def "should support long numbers as Entity Id "(){
        given:
        def javers = javers().build()
        def longId = 1000000000L*1000
        def category = new Category(longId)

        when:
        javers.commit("author",category)

        then:
        javers.getLatestSnapshot(longId, Category).get().globalId.cdoId == longId
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
            assert it.affectedGlobalId.typeName == DummyAddress.name
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
                    (1..5).collect{ new DummyAddress(city: "London${it}")}
                     + new DummyPoint(1,2), //noise
                    (1..5).collect{ new SnapshotEntity(id:1,valueObjectRef: new DummyAddress(city: "London${it}")) }
                     + new SnapshotEntity(id:2,valueObjectRef: new DummyAddress(city: "London1")) //noise
                   ]
        query   << [byInstanceId(1, SnapshotEntity).limit(3).build(),
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
            assert it.globalId.typeName == DummyAddress.name
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
            assert it.globalId.typeName == SnapshotEntity.name
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
             assert it.globalId.typeName == SnapshotEntity.name
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
            assert it.globalId.typeName == DummyAddress.name
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
                byInstanceId(1, SnapshotEntity).andProperty("intProperty").build())

        then:
        snapshots.size() == 2
        snapshots[0].commitId.majorId == 3
        snapshots[1].commitId.majorId == 1

        when: "should find changes"
        def changes = javers.findChanges(
                byInstanceId(1, SnapshotEntity).andProperty("intProperty").build())

        then:
        changes.size() == 1
        changes[0] instanceof ValueChange
        changes[0].commitMetadata.get().id.majorId == 3
        changes[0].left == 4
        changes[0].right == 5
    }

    def "should query for LatestSnapshot of Entity"() {
        given:
        javers.commit("login", new SnapshotEntity(id: 1, intProperty: 1))
        javers.commit("login", new SnapshotEntity(id: 1, intProperty: 2))

        when:
        def snapshot = javers.getLatestSnapshot(1, SnapshotEntity).get()

        then:
        snapshot.commitId.majorId == 2
        snapshot.getPropertyValue("intProperty") == 2
    }

    def "should fetch terminal snapshots from the repository"() {
        given:
        def anEntity = new SnapshotEntity(id:1, entityRef: new SnapshotEntity(id:2))
        javers.commit("author", anEntity)
        javers.commitShallowDelete("author", anEntity)

        when:
        def snapshots = javers.findSnapshots(byInstanceId(1, SnapshotEntity).build())

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
            byInstanceId(1,SnapshotEntity).andProperty("intProperty").build())

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
                                     mapOfValues: [(new LocalDate(2001,1,1)):1.1],
                                     mapOfGenericValues: [("enumSet"):EnumSet.of(DummyEnum.val1, DummyEnum.val2)])
        javers.commit("author", cdo) //v. 1
        cdo.intProperty = 5
        javers.commit("author2", cdo) //v. 2

        when:
        def snapshots = javers.findSnapshots(byInstanceId(1, SnapshotEntity).build())

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
                                            intProperty:5,
                                            mapOfGenericValues: [("enumSet"):EnumSet.of(DummyEnum.val1, DummyEnum.val2)]])
        //assert metadata
        with(snapshots[0]) {
             commitId.value() == "2.0"
             commitMetadata.author == "author2"
             commitMetadata.commitDate
             changed.size() == 1
             changed[0] == "intProperty"
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
        def history = javers.findChanges(byInstanceId("John", DummyUser).build())

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

    def "should commit and read subsequent snapshots from repository"() {
        given:
        def cdo = new SnapshotEntity(id: 1)


        expect:
        (1..25).each {
            cdo.intProperty = it
            javers.commit("login", cdo)
            def snap = javers.findSnapshots(byInstanceId(1, SnapshotEntity).build())[0]
            assert snap.getPropertyValue("intProperty") == it
        }
    }

    def "should do diff and persist commit when class has complex Generic fields inherited from Generic superclass"() {
        given:
        javers.commit("author", new ConcreteWithActualType("a", ["1"]) )
        javers.commit("author", new ConcreteWithActualType("a", ["1","2"]) )

        when:
        def changes = javers.findChanges(QueryBuilder.byClass(ConcreteWithActualType).build())

        then:
        def change = changes[0]
        change.changes[0].index == 1
        change.changes[0].addedValue instanceof String
        change.changes[0].addedValue == "2"
    }

    def "should manage Entity class name refactor when querying using new class with @TypeName retrofitted to old class name"(){
        when:
        javers.commit("author", new OldEntity(id:1, value:5))
        javers.commit("author", new NewEntity(id:1, value:15))

        def changes = javers.findChanges(byInstanceId(1, NewEntity).build())

        then:
        changes.size() == 1
        with(changes.find {it.propertyName == "value"}){
            assert left == 5
            assert right == 15
        }
    }

    def "should manage Entity class name refactor when old and new class uses @TypeName"(){
        when:
        javers.commit("author", new OldEntityWithTypeAlias(id:1, val:5))
        javers.commit("author", new NewEntityWithTypeAlias(id:1, val:15))

        def changes = javers.findChanges(byInstanceId(1, NewEntityWithTypeAlias).build())

        then:
        changes.size() == 1
        with(changes.find {it.propertyName == "val"}){
            assert left == 5
            assert right == 15
        }
    }

    def "should manage ValueObject query when both ValueObject and owner Entity uses @TypeName"(){
        when:
        javers.commit("author", new NewEntityWithTypeAlias(id: 1, valueObject: new NewValueObjectWithTypeAlias(some:5)) )
        javers.commit("author", new NewEntityWithTypeAlias(id: 1, valueObject: new NewValueObjectWithTypeAlias(some:6)) )

        def changes = javers.findChanges(QueryBuilder.byValueObject(NewEntityWithTypeAlias,"valueObject").build())

        then:
        changes.size() == 1
        with (changes.find {it.propertyName == "some"}) {
            assert left == 5
            assert right == 6
        }

    }

    def "should manage ValueObject class name refactor without TypeName when querying by owning Instance"(){
        when:
        javers.commit("author", new EntityWithRefactoredValueObject(id:1, value: new OldValueObject(5,  5)))
        javers.commit("author", new EntityWithRefactoredValueObject(id:1, value: new NewValueObject(5,  10)))
        javers.commit("author", new EntityWithRefactoredValueObject(id:1, value: new NewValueObject(5,  15)))

        def changes = javers.findChanges(QueryBuilder.byValueObject(EntityWithRefactoredValueObject,"value").build())

        then:
        changes.size() == 2
        with(changes.find {it.propertyName == "oldField"}) {
            assert left == 5
            assert right == 0 //removed properties are treated as nulls
        }
        with(changes.find {it.propertyName == "newField"}) {
            assert left == 10
            assert right == 15
        }
    }

    def "should manage ValueObject class name refactor when querying using new class with @TypeName retrofitted to old class name"(){
        when:
        javers.commit("author", new EntityWithRefactoredValueObject(id:1, value: new OldValueObject(5,  10)))
        javers.commit("author", new EntityWithRefactoredValueObject(id:1, value: new NewNamedValueObject(6,  10)))

        def changes = javers.findChanges(QueryBuilder.byClass(NewNamedValueObject).build())

        then:
        changes.size() == 2
        with(changes.find {it.propertyName == "oldField"}) {
            assert left == 10
            assert right == 0 //removed properties are treated as nulls
        }
        with(changes.find {it.propertyName == "someValue"}) {
            assert left == 5
            assert right == 6
        }
    }

    def "should load Snapshot with @TypeName of concrete (used) ValueObject"(){
        given:
        javers.commit("author", new EntityWithRefactoredValueObject(id:1, value: new NewNamedValueObject(6,  10)))

        when:
        def snapshot = javers.findSnapshots(QueryBuilder.byClass(NewNamedValueObject).build())[0]

        then:
        snapshot.globalId.typeName.endsWith("OldValueObject")
    }

    def "should use dateProvider.now() as a commitDate"() {
        given:
        LocalDateTime now = LocalDateTime.parse('2016-01-01T12:12')

        when:
        fakeDateProvider.set(now)
        javers.commit("author", new SnapshotEntity(id: 1))
        CdoSnapshot snapshot = javers.getLatestSnapshot(1, SnapshotEntity).get()
        LocalDateTime commitDate = snapshot.commitMetadata.commitDate

        then:
        now == commitDate
    }

    @Unroll
    def "should query for Entity snapshots with time range filter - #what"() {
        given:
        (1..5).each{
            def entity =  new SnapshotEntity(id: 1, intProperty: it)
            fakeDateProvider.set( new LocalDateTime(2015,01,1,it,0) )
            javers.commit('author', entity)
        }

        when:
        def snapshots = javers.findSnapshots(query)
        def commitDates = snapshots.commitMetadata.commitDate

        then:
        commitDates == expectedCommitDates

        where:
        what << ['date from','date to','date in time range']
        query << [
            byInstanceId(1, SnapshotEntity).from(new LocalDateTime(2015,01,1,3,0)).build(),
            byInstanceId(1, SnapshotEntity).to(new LocalDateTime(2015,01,1,3,0)).build(),
            byInstanceId(1, SnapshotEntity).from(new LocalDateTime(2015,01,1,2,0))
                                           .to(new LocalDateTime(2015,01,1,4,0)).build()
        ]
        expectedCommitDates << [
            (5..3).collect{new LocalDateTime(2015,01,1,it,0)},
            (3..1).collect{new LocalDateTime(2015,01,1,it,0)},
            (4..2).collect{new LocalDateTime(2015,01,1,it,0)}
        ]
    }

    @Unroll
    def "should query for Entity snapshots with skipped results, #what"() {
        given:
        (19..1).each{
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: it))
        }

        when:
        def snapshots = javers.findSnapshots(query)
        def intPropertyValues = snapshots.collect { it.getPropertyValue("intProperty") }

        then:
        intPropertyValues == expectedIntPropertyValues


        where:
        query << [
            byInstanceId(1, SnapshotEntity).skip(0).limit(5).build(),
            byInstanceId(1, SnapshotEntity).skip(5).limit(5).build(),
            byInstanceId(1, SnapshotEntity).skip(15).limit(5).build(),
            byInstanceId(1, SnapshotEntity).skip(20).limit(5).build(),
            byInstanceId(1, SnapshotEntity).skip(5).build()
        ]

        expectedIntPropertyValues << [
            1..5,
            6..10,
            16..19,
            [],
            6..19
        ]

        what << ["first page",
                 "second page",
                 "last page",
                 "too much skip page",
                 "skip without limit"
        ]
    }

    def "should increment Entity snapshot version number"(){
      when:
      javers.commit("author", new SnapshotEntity(id: 1, intProperty: 1))
      javers.commit("author", new SnapshotEntity(id: 1, intProperty: 2))

      then:
      def snapshots = javers.findSnapshots(byInstanceId(1, SnapshotEntity).build())
      snapshots[0].version == 2
      snapshots[1].version == 1

    }

    @Unroll
    def "should query for Entity snapshot with given commit id"() {
        given:
        def commits = (0..10).collect { javers.commit("author", new SnapshotEntity(id: 1, intProperty: it)) }
        def searchedCommitId = commits[7].id

        when:
        def query = createQuery(searchedCommitId)
        def snapshots = javers.findSnapshots(query)

        then:
        snapshots.commitId == [searchedCommitId]

        where:
        createQuery << [
            { commitId -> byClass(SnapshotEntity).withCommitId(commitId).build() },
            { commitId -> byInstanceId(1, SnapshotEntity).withCommitId(commitId).build() },
            { commitId -> byClass(SnapshotEntity).withCommitId(commitId.valueAsNumber()).build() },
            { commitId -> byInstanceId(1, SnapshotEntity).withCommitId(commitId.valueAsNumber()).build() }
        ]
    }

}