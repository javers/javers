package org.javers.core

import org.javers.common.date.FakeDateProvider
import org.javers.common.reflection.ConcreteWithActualType
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.examples.typeNames.*
import org.javers.core.model.*
import org.javers.core.model.SnapshotEntity.DummyEnum
import org.javers.core.snapshot.SnapshotsAssert
import org.javers.repository.api.JaversRepository
import org.javers.repository.api.SnapshotIdentifier
import org.javers.repository.inmemory.InMemoryRepository
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate
import java.time.LocalDateTime

import static groovyx.gpars.GParsPool.withPool
import static org.javers.core.JaversBuilder.javers
import static org.javers.repository.jql.InstanceIdDTO.instanceId
import static org.javers.repository.jql.QueryBuilder.*
import static org.javers.repository.jql.UnboundedValueObjectIdDTO.unboundedValueObjectId
import static org.javers.repository.jql.ValueObjectIdDTO.valueObjectId

class JaversRepositoryE2ETest extends Specification {
    protected FakeDateProvider fakeDateProvider
    protected JaversRepository repository
    protected Javers javers
    protected JaversTestBuilder javersTestBuilder

    def setup() {
        fakeDateProvider = new FakeDateProvider()
        repository = prepareJaversRepository()
        javers = javers().withDateTimeProvider(fakeDateProvider).registerJaversRepository(repository).build()
        javersTestBuilder = JaversTestBuilder.javersTestAssembly()
    }

    protected JaversRepository prepareJaversRepository() {
        return new InMemoryRepository();
    }

    def "should persist various primitive types"(){
      given:
      def s = new PrimitiveEntity(id:1)

      when:
      javers.commit("author",s)
      s.intField = 10
      s.longField = 10
      s.doubleField = 1.1
      s.floatField = 1.1
      s.charField = 'c'
      s.byteField = 10
      s.shortField = 10
      s.booleanField = true
      s.IntegerField = 10
      s.LongField = 10
      s.DoubleField = 1.1
      s.FloatField = 1.1
      s.ByteField = 10
      s.ShortField = 10
      s.BooleanField = true
      javers.commit("author",s)

      then:
      javers.findChanges(QueryBuilder.anyDomainObject().build()).size() == 15
    }

    def "should support EmbeddedId as Entity Id"(){
      given:
      def cdo  = new DummyEntityWithEmbeddedId(point: new DummyPoint(1,2), someVal: 5)

      when:
      javers.commit("author", cdo)

      then:
      def snapshot = javers.getLatestSnapshot(new DummyPoint(1,2), DummyEntityWithEmbeddedId).get()
      snapshot.globalId.cdoId == new DummyPoint(1,2)
    }

    def "should support long numbers as Entity Id "(){
        given:
        def longId = 1000000000L*1000
        def category = new CategoryC(longId)

        when:
        javers.commit("author",category)

        then:
        javers.getLatestSnapshot(longId, CategoryC).get().globalId.cdoId == longId
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

    def "should query for nested ValueObject changes"(){
      given:
      def user = new DummyUserDetails(id:1, dummyAddress:
              new DummyAddress(networkAddress: new DummyNetworkAddress(address: "a")))

      javers.commit("author", user)
      user.dummyAddress.networkAddress.address = "b"
      javers.commit("author", user)

      when:
      def changes = javers.findChanges(QueryBuilder.byValueObjectId(1, DummyUserDetails,
              "dummyAddress/networkAddress").build())

      then:
      changes.size() == 1
      changes[0].left == "a"
      changes[0].right == "b"
    }

    def "should query for changes on nested ValueObjects stored in a list"(){
        given:
        def user = new DummyUserDetails(
            id:1,
            addressList: [new DummyAddress(networkAddress: new DummyNetworkAddress(address: "a"))])

        javers.commit("author", user)
        user.addressList[0].networkAddress.address = "b"
        javers.commit("author", user)

        when:
        def changes = javers.findChanges(QueryBuilder.byValueObjectId(1, DummyUserDetails,
                "addressList/0/networkAddress").build())

        then:
        changes.size() == 1
        changes[0].left == "a"
        changes[0].right == "b"
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
        javers.commit( "author", new SnapshotEntity(id:1, intProperty: 1, dob: LocalDate.now()) ) //noise
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

    @Unroll
    def "should query for snapshots by multiple classes"() {
        given:
        javers.commit("author", new DummyUser(name: "Alice", dummyUserDetails: new DummyUserDetails(id: 66)))
        javers.commit("author", new DummyUser(name: "Bob"))
        javers.commit("author", new SnapshotEntity(id: 1, valueObjectRef: new DummyAddress(city: "Berlin")))
        javers.commit("author", new SnapshotEntity(id: 2))

        when:
        def snapshots = javers.findSnapshots(query)

        then:
        snapshots.size() == expectedGlobalIds.size()
        snapshots.every { snapshot -> snapshot.globalId in expectedGlobalIds }

        where:
        query << [
            QueryBuilder.byClass(DummyUser, SnapshotEntity).build(),
            QueryBuilder.byClass(DummyUserDetails, DummyAddress).build(),
            QueryBuilder.byClass(DummyUser, DummyUserDetails, SnapshotEntity, DummyAddress).build(),
        ]
        expectedGlobalIds << [
            [instanceId("Alice", DummyUser), instanceId("Bob", DummyUser), instanceId(1, SnapshotEntity), instanceId(2, SnapshotEntity)],
            [instanceId(66, DummyUserDetails), valueObjectId(1, SnapshotEntity, "valueObjectRef")],
            [instanceId("Alice", DummyUser), instanceId("Bob", DummyUser), instanceId(1, SnapshotEntity), instanceId(2, SnapshotEntity),
             instanceId(66, DummyUserDetails), valueObjectId(1, SnapshotEntity, "valueObjectRef")],
        ]
    }

    def "should query for Entity snapshots and changes by given instance"() {
        given:
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 4))
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 5))
        javers.commit("author", new SnapshotEntity(id:2, intProperty: 4))

        expect:
        javers.findSnapshots(byInstance(new SnapshotEntity(id:1)).build()).size() == 2
        javers.findChanges(byInstance(new SnapshotEntity(id:1)).build()).size() == 1
    }

    def "should query for Entity snapshots and changes by GlobalId and changed property"() {
        given:
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 4))
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 4, dob : LocalDate.now()))
        javers.commit("author", new SnapshotEntity(id:1, intProperty: 5, dob : LocalDate.now()))
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
        def user = new DummyUser(name:"John",age:18)
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

    def '''should use dateProvider.now() as a commitDate and
           should serialize and deserialize commitDate as local datetime'''() {
        given:
        def now = LocalDateTime.parse('2016-01-01T12:12')
        fakeDateProvider.set(now)

        when:
        javers.commit("author", new SnapshotEntity(id: 1))
        def snapshot = javers.getLatestSnapshot(1, SnapshotEntity).get()

        then:
        snapshot.commitMetadata.commitDate == now
    }

    @Unroll
    def "should query for Entity snapshots with time range filter - #what"() {
        given:
        (1..5).each{
            def entity =  new SnapshotEntity(id: 1, intProperty: it)
            fakeDateProvider.set( LocalDateTime.of(2015,01,1,it,0) )
            javers.commit('author', entity)
        }

        when:
        def snapshots = javers.findSnapshots(query)
        def commitDates = snapshots.commitMetadata.commitDate

        then:
        commitDates == expectedCommitDates

        where:
        what << ['util from','util to','util in time range']
        query << [
            byInstanceId(1, SnapshotEntity).from(LocalDateTime.of(2015,01,1,3,0)).build(),
            byInstanceId(1, SnapshotEntity).to(LocalDateTime.of(2015,01,1,3,0)).build(),
            byInstanceId(1, SnapshotEntity).from(LocalDateTime.of(2015,01,1,2,0))
                                           .to(LocalDateTime.of(2015,01,1,4,0)).build()
        ]
        expectedCommitDates << [
            (5..3).collect{LocalDateTime.of(2015,01,1,it,0)},
            (3..1).collect{LocalDateTime.of(2015,01,1,it,0)},
            (4..2).collect{LocalDateTime.of(2015,01,1,it,0)}
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

    @Unroll
    def "should query for Entity snapshot with given version"() {
        given:
        (1..10).each { javers.commit("author", new SnapshotEntity(id: 1, intProperty: it)) }

        when:
        def snapshots = javers.findSnapshots(query)

        then:
        snapshots.size() == 1
        snapshots[0].getPropertyValue('intProperty') == 5

        where:
        query << [
            byClass(SnapshotEntity).withVersion(5).build(),
            byInstanceId(1, SnapshotEntity).withVersion(5).build()
        ]
    }

    def "should retrieve snapshots with specified identifiers"() {
        given:
        (1..10).each {
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: it))
            javers.commit("author", new SnapshotEntity(id: 2, intProperty: it))
            javers.commit("author", new SnapshotEntity(id: 3, intProperty: it))
        }

        def snapshotIdentifiers = [
            new SnapshotIdentifier(javersTestBuilder.instanceId(new SnapshotEntity(id: 1)), 3),
            new SnapshotIdentifier(javersTestBuilder.instanceId(new SnapshotEntity(id: 3)), 7),
            new SnapshotIdentifier(javersTestBuilder.instanceId(new SnapshotEntity(id: 2)), 1),
            new SnapshotIdentifier(javersTestBuilder.instanceId(new SnapshotEntity(id: 1)), 10)
        ]

        when:
        def snapshots = repository.getSnapshots(snapshotIdentifiers)

        then:
        assert snapshots.size() == snapshotIdentifiers.size()
        snapshotIdentifiers.each { desc ->
            assert snapshots.find( { snap -> snap.globalId == desc.globalId && snap.version == desc.version } )
        }
    }

    def "should cope with query for 1000 different snapshots"() {
        given:
        (1..1000).each {
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: it))
        }

        def snapshotIdentifiers = (1..1000).collect {
            new SnapshotIdentifier(javersTestBuilder.instanceId(new SnapshotEntity(id: 1)), it)
        }

        when:
        def snapshots = repository.getSnapshots(snapshotIdentifiers)

        then:
        assert snapshots.size() == snapshotIdentifiers.size()
    }

    def "should cope with query for snapshots with empty collection of snapshot ids"() {
        given:
        javers.commit("author", new SnapshotEntity(id: 1, intProperty: 1))

        when:
        def snapshots = repository.getSnapshots([])

        then:
        assert snapshots.size() == 0
    }

    def "should treat refactored VOs as different versions of the same client's domain object"(){
        given:
        javers.commit('author', new EntityWithRefactoredValueObject(id:1, value: new OldValueObject(5, 5)))
        javers.commit('author', new EntityWithRefactoredValueObject(id:1, value: new NewValueObject(5, 10)))

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byValueObject(EntityWithRefactoredValueObject,'value').build())

        then:
        snapshots.version == [2, 1]
    }

    @Unroll
    def "should find all changes introduced by all snapshots specified in query (limit: #limit)"() {
        given:
        javers.commit("author", new SnapshotEntity(id: 1))
        (1..100).each {
            def entity = new SnapshotEntity(id: 1, intProperty: it)
            javers.commit("author", entity)

            entity.dob = LocalDate.now()
            javers.commit("author", entity)
        }

        when:
        def query = byInstanceId(1, SnapshotEntity).andProperty('intProperty').limit(limit).build()
        def snapshots = javers.findSnapshots(query)
        def changes = javers.findChanges(query)

        then:
        assert snapshots.size() == changes.size()
        assert changes.first().left == snapshots.first().state.getPropertyValue('intProperty') - 1
        assert changes.first().right == snapshots.first().state.getPropertyValue('intProperty')
        assert changes.last().left == snapshots.last().state.getPropertyValue('intProperty') - 1
        assert changes.last().right == snapshots.last().state.getPropertyValue('intProperty')

        where:
        limit << [1, 50, 99, 200]
    }

    @Unroll
    def "should query for #what snapshot committed by a given author"() {
        given:
        (1..4).each {
            def author = it % 2 == 0 ? "Jim" : "Pam";
            javers.commit(author, new SnapshotEntity(id: it))
            javers.commit(author, new DummyUserDetails(id: it))
        }

        when:
        def snapshots = javers.findSnapshots(query)

        then:
        snapshots*.globalId == expectedResult

        where:
        what << ["Entity", "any"]
        query << [
            byClass(SnapshotEntity).byAuthor("Jim").build(),
            anyDomainObject().byAuthor("Jim").build()
        ]
        expectedResult << [
            [instanceId(4, SnapshotEntity), instanceId(2, SnapshotEntity)],
            [instanceId(4, DummyUserDetails), instanceId(4, SnapshotEntity),
             instanceId(2, DummyUserDetails), instanceId(2, SnapshotEntity)]
        ]
    }

    def "should return empty map of commit properties if snapshot was commited without properties"() {
        given:
        javers.commit("author", new SnapshotEntity(id :1))

        when:
        def snapshot = javers.findSnapshots(byInstanceId(1, SnapshotEntity).build()).first()

        then:
        assert snapshot.commitMetadata.properties.isEmpty()
    }

    def "should return committed properties"() {
        given:
        def commitProperties = [
            "tenant": "ACME",
            "sessionId": "1234567890",
            "device": "smartwatch",
            "yet another property name": "yet another property value",
        ]
        javers.commit("author", new SnapshotEntity(id :1), commitProperties)

        when:
        def snapshot = javers.findSnapshots(byInstanceId(1, SnapshotEntity).build()).first()

        then:
        assert snapshot.commitMetadata.properties["tenant"] == "ACME"
        assert snapshot.commitMetadata.properties["sessionId"] == "1234567890"
        assert snapshot.commitMetadata.properties["device"] == "smartwatch"
        assert snapshot.commitMetadata.properties["yet another property name"] == "yet another property value"
    }

    @Unroll
    def "should retrieve snapshots with specified commit properties"() {
        given:
        javers.commit("author", new SnapshotEntity(id: 1), [ "tenant" : "ACME", "browser": "IE" ])
        javers.commit("author", new SnapshotEntity(id: 2), [ "tenant" : "Dunder Mifflin", "browser": "IE" ])
        javers.commit("author", new SnapshotEntity(id: 3), [ "tenant" : "ACME", "browser": "Safari" ])
        javers.commit("author", new SnapshotEntity(id: 4), [ "tenant" : "Dunder Mifflin", "browser": "Safari" ])
        javers.commit("author", new SnapshotEntity(id: 5), [ "tenant" : "Dunder Mifflin", "browser": "Chrome" ])

        when:
        def snapshots = javers.findSnapshots(query)

        then:
        assert snapshots*.getPropertyValue("id") as Set == expectedSnapshotIds as Set

        where:
        query << [
            byClass(SnapshotEntity).withCommitProperty("tenant", "Dunder Mifflin").build(),
            byClass(SnapshotEntity).withCommitProperty("browser", "Safari").build(),
            byClass(SnapshotEntity).withCommitProperty("tenant", "ACME").withCommitProperty("browser", "IE").build(),
            byClass(SnapshotEntity).withCommitProperty("tenant", "ACME").withCommitProperty("browser", "Chrome").build()
        ]
        expectedSnapshotIds << [
            [2, 4, 5],
            [3, 4],
            [1],
            []
        ]
    }

    def "should handle special characters in commit properties filter"() {
        given:
        javers.commit("author", new SnapshotEntity(id: 1), ["specialCharacters": ""])
        javers.commit("author", new SnapshotEntity(id: 2), ["specialCharacters": "!@#\$%^&*()-_=+[{]};:'\"\\|`~,<.>/?§£"])

        when:
        def snapshots = javers.findSnapshots(byClass(SnapshotEntity).withCommitProperty("specialCharacters", "!@#\$%^&*()-_=+[{]};:'\"\\|`~,<.>/?§£").build())

        then:
        assert snapshots.size() == 1
        assert snapshots[0].getPropertyValue("id") == 2
    }

    def "should query withChildValueObjects for snapshots and changes by InstanceId"() {
        given:
        def london1v1 = new DummyAddress(city: "London", networkAddress: new DummyNetworkAddress(address: "v1"))
        def london2v1 = new DummyAddress(city: "London 2", networkAddress: new DummyNetworkAddress(address: "v1"))
        def london2v2 = new DummyAddress(city: "London 2", networkAddress: new DummyNetworkAddress(address: "v2"))

        def objects = [
            new SnapshotEntity(id:1, valueObjectRef: london1v1),
            new SnapshotEntity(id:1, valueObjectRef: london2v1),
            new SnapshotEntity(id:1, valueObjectRef: london2v2) ,
            new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city: "Paris")) , //noise
            new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city: "Paris 2")) //noise
        ]
        objects.each {
            javers.commit("author", it)
        }

        def query = QueryBuilder.byInstanceId(1, SnapshotEntity).withChildValueObjects().build()

        when: "snapshots query"
        def snapshots = javers.findSnapshots(query)

        then:
        snapshots.each {
            assert it.globalId == instanceId(1, SnapshotEntity) ||
                    it.globalId.ownerId == instanceId(1, SnapshotEntity)
        }
        snapshots.size() == 5

        when: "changes query"
        def changes = javers.findChanges(query)

        then:
        changes.each {
            println it
        }
        changes.size() == 2
    }

    def "should query withChildValueObjects for snapshots and changes by Entity type"() {
        given:
        def london = new DummyAddress(city: "London")
        def paris =  new DummyAddress(city: "Paris")
        def paris2 = new DummyAddress(city: "Paris",
                networkAddress: new DummyNetworkAddress(address: "v2"))

        def objects = [
                new SnapshotEntity(id: 1, valueObjectRef: london),
                new SnapshotEntity(id: 1, valueObjectRef: paris),
                new SnapshotEntity(id: 1, valueObjectRef: paris2),
                new SnapshotEntity(id: 1, valueObjectRef: paris2, listOfValueObjects: [london]),
                new SnapshotEntity(id: 1, valueObjectRef: paris2, listOfValueObjects: [london, paris]),
                new DummyUserDetails(id: 1, dummyAddress: paris) //noise
        ]
        objects.each {
            javers.commit("author", it)
        }

        def query = QueryBuilder.byClass(SnapshotEntity).withChildValueObjects().build()

        when: "snapshots query"
        def snapshots = javers.findSnapshots(query)

        then:
        snapshots.each {
            assert it.globalId.typeName == SnapshotEntity.name ||
                   it.globalId.ownerId.typeName == SnapshotEntity.name
        }
        snapshots.size() == 9

        when: "changes query"
        def changes = javers.findChanges(query)

        then:
        changes.each {
            println it
        }
        changes.size() == 4
    }

    def "should provide cluster-friendly commitId generator"(){
        given:
        def threads = 10
        def javersRepo = new InMemoryRepository()
        when:
        withPool threads, {
            (1..threads).collectParallel {
                def javers = JaversBuilder.javers()
                        .registerJaversRepository(javersRepo)
                        .withCommitIdGenerator(CommitIdGenerator.RANDOM)
                        .build()
                javers.commit("author", new SnapshotEntity(id: it))
            }
        }

        then:
        def javers = JaversBuilder.javers().registerJaversRepository(javersRepo).build()
        def snapshots = javers.findSnapshots(QueryBuilder.anyDomainObject().build())
        (snapshots.collect{it -> it.commitId} as Set).size() == threads
    }

    def "should not persist commits with zero snapshots" () {
        given:
        def anEntity = new SnapshotEntity(id: 1, intProperty: 100)

        when:
        def commit = javers.commit("author", anEntity)
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())

        then:
        snapshots.size() == 1
        repository.getHeadId() == commit.getId()

        when: "should not be persisted"
        javers.commit("author", anEntity)

        then:
        repository.getHeadId() == commit.getId()
    }
}