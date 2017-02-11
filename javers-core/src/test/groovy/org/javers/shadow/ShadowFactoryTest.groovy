package org.javers.shadow

import com.google.common.collect.HashMultiset
import org.javers.core.Javers
import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.model.DummyAddress
import org.javers.core.model.PrimitiveEntity
import org.javers.core.model.SnapshotEntity
import org.javers.core.model.SomeEnum
import org.javers.guava.MultimapBuilder
import org.javers.repository.jql.QueryBuilder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

import static java.lang.System.identityHashCode
import static java.time.LocalDate.now

/**
 * @author bartosz.walacik
 */
class ShadowFactoryTest extends Specification {

    @Shared JaversTestBuilder javersTestAssembly = JaversTestBuilder.javersTestAssembly()
    @Shared ShadowFactory shadowFactory = javersTestAssembly.shadowFactory
    @Shared Javers javers = javersTestAssembly.javers()

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
                 v2.multiMapOfPrimitives = MultimapBuilder.create([a:['a','b','c']])
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
      def shadow = shadowFactory.createShadow(snapshots[0], { id ->
          if (id instanceof InstanceId && id.cdoId == 2) {
              return javers.findSnapshots(QueryBuilder.byInstanceId(2, SnapshotEntity).build())[0]
          }
          null
      })

      then:
      shadow instanceof SnapshotEntity

      shadow.valueObjectRef == null

      shadow.entityRef instanceof SnapshotEntity
      shadow.entityRef.id == 2
      shadow.entityRef.intProperty == 2
    }
}
