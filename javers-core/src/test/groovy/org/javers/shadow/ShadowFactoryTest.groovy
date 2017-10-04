package org.javers.shadow

import com.google.common.collect.HashMultiset
import org.javers.core.Javers
import org.javers.core.JaversTestBuilder
import org.javers.core.examples.typeNames.NewEntity
import org.javers.core.examples.typeNames.NewEntityWithTypeAlias
import org.javers.core.examples.typeNames.OldEntityWithTypeAlias
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.PropertyName
import org.javers.core.metamodel.object.CdoSnapshot
import org.javers.core.metamodel.object.CdoSnapshotBuilder
import org.javers.core.metamodel.object.CdoSnapshotState
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.model.CategoryC
import org.javers.core.model.DummyAddress
import org.javers.core.model.PhoneWithShallowCategory
import org.javers.core.model.PrimitiveEntity
import org.javers.core.model.ShallowPhone
import org.javers.core.model.SnapshotEntity
import org.javers.core.model.SomeEnum
import org.javers.guava.MultimapBuilder
import org.javers.repository.jql.QueryBuilder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import java.time.LocalDate
import java.util.function.BiFunction
import static java.lang.System.identityHashCode
import static java.time.LocalDate.now

/**
 * @author bartosz.walacik
 */
abstract class ShadowFactoryTest extends Specification {

    @Shared JaversTestBuilder javersTestAssembly
    @Shared ShadowFactory shadowFactory
    @Shared Javers javers

    def setupSpec() {
        javersTestAssembly = JaversTestBuilder.javersTestAssembly()
        shadowFactory = javersTestAssembly.shadowFactory
        javers = javersTestAssembly.javers()
    }

    @Unroll
    def "should create Shadows with #what"(){
      when:
      javers.commit("author",v1())
      javers.commit("author",v2())

      def snapshots = javers.findSnapshots(QueryBuilder.anyDomainObject().build())
      def shadowV1 = shadowFactory.createShadow(snapshots[1])
      def shadowV2 = shadowFactory.createShadow(snapshots[0])

      then:
      shadowV1 == v1()
      identityHashCode(shadowV1) != identityHashCode(v1())

      shadowV2 == v2()
      identityHashCode(shadowV1) != identityHashCode(v2())

      where:
      what << ['primitive fields',
               'Collections and Values',
               'Multisets and Multimaps'
              ]
      v1 << [{ new PrimitiveEntity()},
             { new SnapshotEntity() },
             { new SnapshotEntity() }
            ]
      v2 << [{   def v2 = new PrimitiveEntity()
                 v2.intField = 10
                 v2.longField = 10
                 v2.doubleField = 1.1
                 v2.floatField = 1.1
                 v2.charField = 'a'
                 v2.byteField = 10
                 v2.shortField = 10
                 v2.booleanField = true
                 v2.IntegerField = 10
                 v2.LongField = 10
                 v2.DoubleField = 1.1
                 v2.FloatField = 1.1
                 v2.ByteField = 10
                 v2.ShortField = 10
                 v2.BooleanField = true
                 v2.someEnum = SomeEnum.A
                 v2
             },
             {   def v2 = new SnapshotEntity()
                 v2.dob = now()
                 v2.arrayOfInts = [1,2]
                 v2.arrayOfIntegers = [1,2]
                 v2.arrayOfDates = [now()] * 2
                 v2.setOfIntegers = [1,2] as Set
                 v2.setOfDates = [now(), new LocalDate(2017,1,1)] as Set
                 v2.listOfIntegers = [1,2]
                 v2.listOfDates = [now(), new LocalDate(2017,1,1)]
                 v2.optionalDate = Optional.of(now())
                 v2.optionalInteger = Optional.of(1)
                 v2.mapOfPrimitives = ['a':1, 'b':2]
                 v2.mapOfValues = [(now()):1.1]
                 v2
              },
              {  def v2 = new SnapshotEntity()
                 v2.multiSetOfPrimitives = HashMultiset.create(['a','a'])
                 v2.multiMapOfPrimitives = MultimapBuilder.create([a:['a', 'b', 'c']])
                 v2
             }]
    }

    def "should resolve Entity ref in a simple case "(){
      given:
      def e = new SnapshotEntity(id:1,
                                 entityRef: new SnapshotEntity(id:2, intProperty:2),
                                 valueObjectRef: new DummyAddress("unavailable ref"))
      javers.commit("author", e)

      when:
      def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())
      def shadow = shadowFactory.createShadow(snapshots[0], snapshotEntitySnapshotSupplier())

      then:
      shadow instanceof SnapshotEntity
      shadow.id == 1

      shadow.valueObjectRef == null

      shadow.entityRef instanceof SnapshotEntity
      shadow.entityRef.id == 2
      shadow.entityRef.intProperty == 2
    }

    def "should resolve Entity with ShallowReference"(){
      given:
      def e = new SnapshotEntity(id:1, shallowPhone:new ShallowPhone(11, "123", new CategoryC(1, "some")))
      javers.commit("author", e)

      when:
      def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())
      def shadow = shadowFactory.createShadow(snapshots[0], byIdSupplier())

      then:
      shadow.shallowPhone instanceof ShallowPhone
      shadow.shallowPhone.id == 11
      !shadow.shallowPhone.number
      !shadow.shallowPhone.category
    }

    def "should resolve Property with ShallowReference"() {
      def e = new PhoneWithShallowCategory(id:1, shallowCategory: new CategoryC(2, "cat1"))
      javers.commit("author", e)

      when:
      def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, PhoneWithShallowCategory).build())
      def shadow = shadowFactory.createShadow(snapshots[0], byIdSupplier())

      then:
      shadow.shallowCategory instanceof CategoryC
      shadow.shallowCategory.id == 2
      !shadow.shallowCategory.name
    }

    def "should support circular references"(){
        given:
        def e = new SnapshotEntity(id:1, entityRef: new SnapshotEntity(id:2, intProperty:2))
        e.entityRef.entityRef = e
        javers.commit("author", e)

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())
        def shadow = shadowFactory.createShadow(snapshots[0], byIdSupplier())

        then:
        shadow instanceof SnapshotEntity
        shadow.id == 1
        shadow.entityRef instanceof SnapshotEntity
        shadow.entityRef.id == 2
        identityHashCode(shadow.entityRef.entityRef) == identityHashCode(shadow)
    }

    @Unroll
    def "should support #container of Entities"(){
        given:
        javers.commit("author", cdo)

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())
        def shadow = shadowFactory.createShadow(snapshots[0], byIdSupplier())

        then:
        shadow.properties[pName].class.simpleName == className
        shadow.properties[pName].size() == 2

        shadow.properties[pName][0] instanceof SnapshotEntity
        shadow.properties[pName][0].id == 2
        shadow.properties[pName][0].intProperty == 2

        shadow.properties[pName][1] instanceof SnapshotEntity
        shadow.properties[pName][1].id == 3
        shadow.properties[pName][1].intProperty == 3

        where:
        container << ['List', 'Array']
        className << ['ArrayList', 'SnapshotEntity[]']
        cdo << [new SnapshotEntity(id:1,
                        listOfEntities: [new SnapshotEntity(id:2, intProperty:2), new SnapshotEntity(id:3, intProperty:3)]),
                new SnapshotEntity(id:1,
                        arrayOfEntities: [new SnapshotEntity(id:2, intProperty:2), new SnapshotEntity(id:3, intProperty:3)])]
        pName << ['listOfEntities','arrayOfEntities']

    }

    def "should support long chain of references with cycle"(){
      given:
      def cdo = new SnapshotEntity(id:1)
      def node = cdo
      (2..100).each {
          node.entityRef = new SnapshotEntity(id:it)
          node = node.entityRef
      }
      node.entityRef = cdo

      javers.commit("author", cdo)


      when:
      def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())
      def shadow = shadowFactory.createShadow(snapshots[0], byIdSupplier())

      then:
      shadow instanceof SnapshotEntity
      (2..100).each {
          assert shadow.entityRef instanceof SnapshotEntity
          assert shadow.entityRef.id == it
          shadow = shadow.entityRef
      }
      shadow.entityRef.id == 1
    }

    def "should support Set of ValueObjects"(){
        given:
        def cdo = new SnapshotEntity(id:1,
                setOfValueObjects: [new DummyAddress('London'), new DummyAddress('Paris')])
        javers.commit("author", cdo)

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())
        def shadow = shadowFactory.createShadow(snapshots[0], byIdSupplier())

        then:
        shadow.setOfValueObjects instanceof Set
        shadow.setOfValueObjects.size() == 2

        shadow.setOfValueObjects.find{it -> it.city == 'London'} instanceof DummyAddress
        shadow.setOfValueObjects.find{it -> it.city == 'Paris'} instanceof DummyAddress
    }

    @Unroll
    def "should support Map with #content"(){
        given:
        javers.commit("author", cdo)

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())
        def shadow = shadowFactory.createShadow(snapshots[0], byIdSupplier())

        then:
        shadow.properties[pName][expectedKey] == expectedValue

        where:
        content << ["Entities", "mixed content"]
        cdo << [
                new SnapshotEntity(id:1, mapOfEntities: [(new SnapshotEntity(id:2)) : new SnapshotEntity(id:3)]),
                new SnapshotEntity(id:1, mapPrimitiveToEntity: ["key" : new SnapshotEntity(id:3)])
        ]
        pName << ["mapOfEntities", "mapPrimitiveToEntity"]
        expectedKey << [new SnapshotEntity(id:2), "key"]
        expectedValue << [new SnapshotEntity(id:3)] * 2
    }

    def "should support Multimap with mixed content"() {
        given:
        def cdo = new SnapshotEntity(id: 1,
                multiMapPrimitiveToEntity: MultimapBuilder.create(["NY": [new SnapshotEntity(id:2), new SnapshotEntity(id:3)]]))
        javers.commit("author", cdo)

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())
        def shadow = shadowFactory.createShadow(snapshots[0], byIdSupplier())

        then:
        shadow.multiMapPrimitiveToEntity.get("NY") == [new SnapshotEntity(id:2), new SnapshotEntity(id:3)]
    }

    def "should support Multiset with Entities"() {
        given:
        def cdo = new SnapshotEntity(id: 1,
                multiSetOfEntities: HashMultiset.create([new SnapshotEntity(id:2), new SnapshotEntity(id:2)]))
        javers.commit("author", cdo)

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())
        def shadow = shadowFactory.createShadow(snapshots[0], byIdSupplier())

        then:
        shadow.multiSetOfEntities == HashMultiset.create([new SnapshotEntity(id:2), new SnapshotEntity(id:2)])
    }

    def "should manage changed class name"(){
      given:
      def cdo = new OldEntityWithTypeAlias(id:1.0, val:1)
      javers.commit("author", cdo)

      when:
      def snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(1.0, NewEntityWithTypeAlias).build())[0]
      snapshot = simulatePersistence(snapshot)
      def shadow = shadowFactory.createShadow(snapshot, { it -> null })

      then:
      shadow instanceof NewEntityWithTypeAlias
      shadow.val == 1
    }

    def "should skip missing properties"(){
      given:
      def cdo = new NewEntity(id:1)
      javers.commit("author", cdo)

      when:
      def snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(1, NewEntity).build())[0]
      def extendedSnapshot = CdoSnapshotBuilder.emptyCopyOf(snapshot)
            .withState(new CdoSnapshotState([strangeValue:2])).build()

      def shadow = shadowFactory.createShadow(extendedSnapshot, { it -> null })

      then:
      shadow instanceof NewEntity
      !shadow.id
    }

    //deserialize and serialize to simulate real JaversRepository
    CdoSnapshot simulatePersistence(CdoSnapshot snapshot) {
        javers.getJsonConverter().fromJson(javers.getJsonConverter().toJson(snapshot), CdoSnapshot)
    }

    def "should not break on polymorfic Collection"() {
      given:
      def cdo = new SnapshotEntity(id: 1, polymorficList: [new LocalDate(2017,1,1), new LocalDate(2017,1,2) ])
      javers.commit("author", cdo)
      def snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(1, SnapshotEntity).build())[0]
      //serialize & deserialize
      snapshot = javers.getJsonConverter().fromJson(javers.getJsonConverter().toJson(snapshot), CdoSnapshot)

      when:
      def shadow = shadowFactory.createShadow(snapshot, byIdSupplier())

      then: "objects converted to JSON String should be returned"
      shadow.polymorficList == ["2017-01-01", "2017-01-02"]
    }

    static class EntityWithPropertyName {
        @Id int id
        @PropertyName("otherField") String someField
        @PropertyName("otherField") String getSomeField() { return someField }
        @Id int getId() { return id }
    }

    def "should use @PropertyName when creating Shadows"(){
        given:
        def e = new EntityWithPropertyName(id:1, someField: "s")
        javers.commit("author", e)

        when:
        EntityWithPropertyName shadow = javers.findShadows(QueryBuilder.byInstance(e).build()).get(0).get()

        then:
        shadow.id == 1
        shadow.someField == "s"
    }

    BiFunction byIdSupplier() {
        return { s, id ->
            if (id instanceof InstanceId) {
                return javers.findSnapshots(QueryBuilder.byInstanceId(id.cdoId, Class.forName(id.typeName)).build())[0]
            } else {
                return javers.findSnapshots(QueryBuilder.byValueObject(SnapshotEntity, id.fragment).build())[0]
            }
        } as BiFunction
    }

    BiFunction snapshotEntitySnapshotSupplier() {
        return { s, id ->
            if (id instanceof InstanceId && id.cdoId) {
                return javers.findSnapshots(QueryBuilder.byInstanceId(id.cdoId, SnapshotEntity).build())[0]
            }
            null
        }
    }
}
