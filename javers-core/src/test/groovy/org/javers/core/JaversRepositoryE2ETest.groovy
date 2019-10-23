package org.javers.core

import org.javers.common.date.DateProvider
import org.javers.common.reflection.ConcreteWithActualType
import org.javers.core.commit.CommitMetadata
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.container.ListChange
import org.javers.core.examples.typeNames.*
import org.javers.core.metamodel.annotation.TypeName
import org.javers.core.model.*
import org.javers.core.model.SnapshotEntity.DummyEnum
import org.javers.repository.api.JaversRepository
import org.javers.repository.api.SnapshotIdentifier
import org.javers.repository.inmemory.InMemoryRepository
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification
import spock.lang.Unroll

import javax.persistence.Id
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

import static groovyx.gpars.GParsPool.withPool
import static GlobalIdTestBuilder.instanceId
import static GlobalIdTestBuilder.unboundedValueObjectId
import static GlobalIdTestBuilder.valueObjectId
import static java.lang.Math.abs
import static java.time.temporal.ChronoUnit.MILLIS
import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.metamodel.object.SnapshotType.INITIAL
import static org.javers.core.metamodel.object.SnapshotType.UPDATE
import static org.javers.repository.jql.QueryBuilder.*

class JaversRepositoryE2ETest extends Specification {
    protected JaversRepository repository
    protected Javers javers
    private DateProvider dateProvider
    private RandomCommitGenerator randomCommitGenerator = null

    void databaseCommit(){
    }

    def setup() {
        buildJaversInstance()
    }

    void buildJaversInstance() {
        dateProvider = prepareDateProvider()
        repository = prepareJaversRepository()
        javers = buildNextJaversInstance(repository)
    }

    Javers buildNextJaversInstance (JaversRepository repository) {
        def javersBuilder = JaversBuilder
                .javers()
                .withDateTimeProvider(dateProvider)
                .registerJaversRepository(repository)

        if (useRandomCommitIdGenerator()) {
            randomCommitGenerator = new RandomCommitGenerator()
            javersBuilder.withCustomCommitIdGenerator(randomCommitGenerator)
        }

        javersBuilder.build()
    }

    protected int commitSeq(CommitMetadata commit) {
        if (useRandomCommitIdGenerator()) {
            return randomCommitGenerator.getSeq(commit.id)
        }
        commit.id.majorId
    }

    protected DateProvider prepareDateProvider() {
        if (useRandomCommitIdGenerator()) {
            return new TikDateProvider()
        }
        new FakeDateProvider()
    }

    protected setNow(ZonedDateTime dateTime) {
        dateProvider.set(dateTime)
    }

    protected JaversRepository prepareJaversRepository() {
        new InMemoryRepository()
    }

    protected boolean useRandomCommitIdGenerator() {
        false
    }

    def 'should persist current LocalDateTime and Instant in CommitMetadata' () {
        given:
        def now = ZonedDateTime.now()
        setNow(now)

        when:
        javers.commit('author', new SnapshotEntity(id: 1))
        def snapshot = javers.getLatestSnapshot(1, SnapshotEntity).get()

        then:
        abs(MILLIS.between(snapshot.commitMetadata.commitDate, now.toLocalDateTime())) <= 1
        snapshot.commitMetadata.commitDateInstant == now.toInstant()
        snapshot.commitMetadata.author == 'author'
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
        commitSeq(changes[0].commitMetadata.get()) == 6
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
        commitSeq(changes[0].commitMetadata.get()) == 3
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
        commitSeq(snapshots[0].commitMetadata) == 5
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
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(DummyAddress)
                .withChangedProperty("city")
                .withSnapshotTypeUpdate().build())

        then:
        snapshots.size() == 1
        snapshots[0].globalId.typeName == DummyAddress.name

        when: "withNewObjectChanges"
        snapshots = javers.findSnapshots(QueryBuilder.byClass(DummyAddress)
                .withChangedProperty("city").build())

        then:
        snapshots.size() == 3
    }

    def "should query for Entity snapshots with snapshotType filter"(){
      given:
      javers.commit( "author", new SnapshotEntity(id:1, intProperty: 1) )
      javers.commit( "author", new SnapshotEntity(id:1, intProperty: 2) )

      when:
      def snapshots = javers.findSnapshots(QueryBuilder.byClass(SnapshotEntity)
              .withSnapshotType(INITIAL).build())

      then:
      snapshots.size() == 1
      commitSeq(snapshots[0].commitMetadata) == 1

      when:
      snapshots = javers.findSnapshots(QueryBuilder.byClass(SnapshotEntity)
            .withSnapshotType(UPDATE).build())

      then:
      snapshots.size() == 1
      commitSeq(snapshots[0].commitMetadata) == 2
    }

    def "should query for Entity snapshots and changes by Entity class and changed property"() {
        given:
        javers.commit( "author", new SnapshotEntity(id:1, intProperty: 1) )
        javers.commit( "author", new SnapshotEntity(id:1, intProperty: 1, dob: LocalDate.now()) ) //noise
        javers.commit( "author", new SnapshotEntity(id:1, intProperty: 2) )
        javers.commit( "author", new DummyAddress() ) //noise
        javers.commit( "author", new SnapshotEntity(id:2, intProperty: 1) )

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byClass(SnapshotEntity)
                .withChangedProperty("intProperty")
                .withSnapshotTypeUpdate().build())

        then:
        true
        snapshots.size() == 1
        commitSeq(snapshots[0].commitMetadata) == 3
        snapshots[0].globalId.value() ==  SnapshotEntity.name+"/1"

        when: "withNewObjectChanges"
        snapshots = javers.findSnapshots(QueryBuilder.byClass(SnapshotEntity)
                .withChangedProperty("intProperty").build())

        then:
        snapshots.size() == 3

        when: "changes query"
        def changes = javers.findChanges(QueryBuilder.byClass(SnapshotEntity)
                .withChangedProperty("intProperty").build())

        then:
        changes.size() == 1
        commitSeq(changes[0].getCommitMetadata().get()) == 3
        changes[0] instanceof ValueChange
        changes[0].propertyName == "intProperty"
        changes[0].left == 1
        changes[0].right == 2
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
        commitSeq(changes[0].commitMetadata.get()) == 5
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
         commitSeq(snapshots[0].commitMetadata) == 2
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
                byInstanceId(1, SnapshotEntity).withChangedProperty("intProperty").build())

        then:
        snapshots.size() == 2
        commitSeq(snapshots[0].commitMetadata) == 3
        commitSeq(snapshots[1].commitMetadata) == 1

        when: "should find changes"
        def changes = javers.findChanges(
                byInstanceId(1, SnapshotEntity).withChangedProperty("intProperty").build())

        then:
        changes.size() == 1
        changes[0] instanceof ValueChange
        commitSeq(changes[0].commitMetadata.get()) == 3
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
        commitSeq(snapshot.commitMetadata) == 2
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
        snapshots.size() == 2
        snapshots[0].globalId.equals(instanceId(1,SnapshotEntity))
        snapshots[0].terminal
        commitSeq(snapshots[0].commitMetadata) == 2

        snapshots[1].globalId.equals(instanceId(1,SnapshotEntity))
        snapshots[1].initial
        commitSeq(snapshots[1].commitMetadata) == 1
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
            byInstanceId(1,SnapshotEntity).withChangedProperty("intProperty").build())

        then:
        changes.size() == n-1
        (0..n-2).each {
            def change = changes[it]
            assert commitSeq(change.commitMetadata.get()) == n-it
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
        def refId = instanceId(2,SnapshotEntity)

        //assert properties
        def snapshot = snapshots.find{it.globalId.cdoId == 1}
        commitSeq(snapshot.commitMetadata) == 2
        snapshot.getPropertyValue('id') == 1
        snapshot.getPropertyValue('entityRef') == refId
        snapshot.getPropertyValue('arrayOfIntegers') == [1,2]
        snapshot.getPropertyValue('listOfDates') == [new LocalDate(2001,1,1), new LocalDate(2001,1,2)]
        snapshot.getPropertyValue('mapOfValues') == [(new LocalDate(2001,1,1)):1.1]
        snapshot.getPropertyValue('intProperty') == 5
        snapshot.getPropertyValue('mapOfGenericValues') == [("enumSet"):EnumSet.of(DummyEnum.val1, DummyEnum.val2)]

        //assert metadata
        with(snapshots[0]) {
             commitSeq(commitMetadata)== 2
             commitMetadata.author == "author2"
             commitMetadata.commitDate
             changed.size() == 1
             changed[0] == "intProperty"
             !initial
        }
        with(snapshots[1]) {
            commitSeq(commitMetadata) == 1
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
        def changes = javers.findChanges(byClass(ConcreteWithActualType).build())

        then:
        def change = changes[0]
        change.changes[0].index == 1
        change.changes[0].addedValue instanceof String
        change.changes[0].addedValue == "2"
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

    def "should load Snapshot with @TypeName of concrete (used) ValueObject"(){
        given:
        javers.commit("author", new EntityWithRefactoredValueObject(id:1, value: new NewNamedValueObject(6,  10)))

        when:
        def snapshot = javers.findSnapshots(QueryBuilder.byClass(NewNamedValueObject).build())[0]

        then:
        snapshot.globalId.typeName.endsWith("OldValueObject")
    }

    @Unroll
    def "should query for Entity snapshots with time range filter - #what"() {
        given:
        (1..5).each{
            def entity =  new SnapshotEntity(id: 1, intProperty: it)
            def now = ZonedDateTime.of(LocalDateTime.of(2015,01,1,it,0), ZoneId.of("UTC"))
            setNow( now )
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

        def javersTestBuilder = javersTestAssembly()
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

    def "should cope with query for 200 different snapshots"() {
        given:
        (1..200).each {
            javers.commit("author", new SnapshotEntity(id: 1, intProperty: it))
        }

        def instanceId = javersTestAssembly().instanceId(new SnapshotEntity(id: 1))
        def snapshotIdentifiers = (1..200).collect {
            new SnapshotIdentifier(instanceId, it)
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
        def query = byInstanceId(1, SnapshotEntity).withChangedProperty('intProperty').limit(limit).build()
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
        def objects = [
          new SnapshotEntity(id:1,
                  valueObjectRef: new DummyAddress(city: "London", networkAddress: new DummyNetworkAddress(address: "v1"))),
          new SnapshotEntity(id:1,
                  valueObjectRef: new DummyAddress(city: "London 2", networkAddress: new DummyNetworkAddress(address: "v1"))),
          new SnapshotEntity(id:1,
                  valueObjectRef: new DummyAddress(city: "London 2", networkAddress: new DummyNetworkAddress(address: "v2"))),
          new SnapshotEntity(id:2, valueObjectRef: new DummyAddress(city: "Paris")) //noise
        ]
        objects.each {
            javers.commit("author", it)
        }

        def query = QueryBuilder.byInstanceId(1, SnapshotEntity).withChildValueObjects().build()

        when: "snapshots query"
        def snapshots = javers.findSnapshots(query)

        then:
        def sName = SnapshotEntity.name
        snapshots.size() == 5
        snapshots[0,1].collect{it.globalId.value()} as Set == [
                 "$sName/1#valueObjectRef/networkAddress",
                 "$sName/1#valueObjectRef"] as Set
        snapshots[2,3,4].collect{it.globalId.value()} as Set == [
                 "$sName/1#valueObjectRef/networkAddress",
                 "$sName/1#valueObjectRef",
                 "$sName/1"
                ] as Set

        when: "changes query"
        def changes = javers.findChanges(query)

        then:
        changes.size() == 2
        changes[0].affectedGlobalId.value() == "$sName/1#valueObjectRef/networkAddress"
        changes[1].affectedGlobalId.value() == "$sName/1#valueObjectRef"
    }

    def "should query withChildValueObjects for snapshots and changes by Entity type"() {
        given:
        def london = new DummyAddress(city: "London")
        def paris = new DummyAddress(city: "Paris", networkAddress: new DummyNetworkAddress(address: "v2"))

        def objects = [
                new SnapshotEntity(id: 1, valueObjectRef: london),
                new SnapshotEntity(id: 1, valueObjectRef: paris, listOfValueObjects: [
                        new DummyAddress(city: "Bologne"),
                        new DummyAddress(city: "Ferrara")]),
                new DummyUserDetails(id: 1, dummyAddress: paris) //noise
        ]
        objects.each {
            javers.commit("author", it)
        }

        def query = QueryBuilder.byClass(SnapshotEntity).withChildValueObjects().build()

        when: "snapshots query"
        def snapshots = javers.findSnapshots(query)

        then:
        def sName = SnapshotEntity.class.name
        snapshots.size() == 7
        snapshots[0,1,2,3,4].collect{it.globalId.value()} as Set == [
                "$sName/1",
                "$sName/1#valueObjectRef",
                "$sName/1#listOfValueObjects/0",
                "$sName/1#listOfValueObjects/1",
                "$sName/1#valueObjectRef/networkAddress"
        ] as Set

        snapshots[5,6].collect{it.globalId.value()} as Set == [
                "$sName/1",
                "$sName/1#valueObjectRef",
        ] as Set

        when: "changes query"
        def changes = javers.findChanges(query)

        then:
        changes.find{it instanceof ListChange}.affectedGlobalId.value() == "$sName/1"
        changes.find{it instanceof ValueChange}.affectedGlobalId.value() == "$sName/1#valueObjectRef"
        changes.find{it instanceof ReferenceChange}.affectedGlobalId.value() == "$sName/1#valueObjectRef"
    }

    def "should persist commits in multiple-instances environment"(){
        given:
        def threads = 10
        when:
        withPool threads, {
            (1..threads).collectParallel {
                def jv = buildNextJaversInstance(repository)
                jv.commit("author", new SnapshotEntity(id: it))
                databaseCommit()
            }
        }

        then:
        def javers = buildNextJaversInstance(repository)
        def snapshots = javers.findSnapshots(QueryBuilder.anyDomainObject().build())
        snapshots.size() == threads
        snapshots.collect{it -> it.getPropertyValue("id")} as Set == (1..threads).collect{it} as Set

        println 'persisted Entity ids: ' + snapshots.collect{it -> it.getPropertyValue("id")}
        println 'persisted commits ids: ' + snapshots.collect{it -> it.commitId}
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

    def "should use name from @PropertyName in commits and queries"(){
        given:
        def javers = JaversBuilder.javers().build()

        when:
        javers.commit("author", new DummyUserDetails(id:1))
        javers.commit("author", new DummyUserDetails(id:1, customizedProperty: 'a'))
        def snapshots = javers.findSnapshots(QueryBuilder.anyDomainObject().withChangedProperty('Customized Property').build())

        then:
        snapshots.size() == 1
        snapshots[0].getPropertyValue('Customized Property') == 'a'
    }

    def "should query by multiple CommitId"(){
      given:
      def entity = new SnapshotEntity(id: 1, valueObjectRef: new DummyAddress(city: "London"))
      def firstCommit = javers.commit("a", entity)

      def lastCommit
      3.times {
          entity.intProperty = it + 1
          lastCommit = javers.commit("a", entity)
      }

      when:
      def snapshots = javers
              .findSnapshots(QueryBuilder.anyDomainObject()
              .withCommitIds( [firstCommit.id.valueAsNumber(), lastCommit.id.valueAsNumber()] )
              .build())

      then:
      snapshots.size() == 3
      snapshots.every{it.commitId == firstCommit.id || it.commitId == lastCommit.id}
    }


    @TypeName("C")
    static class C1 {
        @Id int id
        String value
    }

    @TypeName("C")
    static class C2 {
        @Id int id
        int value
    }

    @TypeName("C")
    static class C21 {
        @Id int id
        List<Integer> value
    }

    @TypeName("C")
    static class C22 {
        @Id int id
        List<String> value
    }

    @TypeName("C")
    static class C3 {
        @Id int id
        C3 value
    }

    @TypeName("C")
    static class C4 extends C1 {
    }

    def "should allow for property type change"(){
      given:
      javers.commit("author", new C1 (id:1, value: "a"))
      javers.commit("author", new C2 (id:1, value: 1))
      javers.commit("author", new C21(id:1, value: [2,1]))
      javers.commit("author", new C22(id:1, value: ["2","1"]))
      javers.commit("author", new C3 (id:1, value: new C3(id:2)))
      javers.commit("author", new C4 (id:1, value: "a"))

      when:
      def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, "C").build())

      then:
      snapshots.size() == 6

      snapshots[0].getPropertyValue("value") == "a"
      snapshots[1].getPropertyValue("value").value() == "C/2"
      snapshots[2].getPropertyValue("value") == ["2","1"]
      snapshots[3].getPropertyValue("value") == [2,1]
      snapshots[4].getPropertyValue("value") == 1
      snapshots[5].getPropertyValue("value") == "a"

      when:
      def changes = javers.findChanges(QueryBuilder.byInstanceId(1, "C").build())

      then:
      println changes.prettyPrint()
      changes.size() == 5
      changes[0] instanceof ValueChange
    }
}