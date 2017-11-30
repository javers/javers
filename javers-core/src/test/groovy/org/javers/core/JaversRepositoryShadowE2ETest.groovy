package org.javers.core

import org.javers.core.examples.typeNames.NewEntity
import org.javers.core.examples.typeNames.NewEntityWithTypeAlias
import org.javers.core.examples.typeNames.OldEntity
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyNetworkAddress
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder

import java.time.LocalDate

import static org.javers.repository.jql.QueryBuilder.byInstanceId

class JaversRepositoryShadowE2ETest extends JaversRepositoryE2ETest {

    def "should not mix COMMIT_DEEP scope with DEEP_PLUS scope"(){
      given:
      def eRef = new SnapshotEntity(id: 2, intProperty: 2)
      def e = new SnapshotEntity(id: 1, intProperty: 1, entityRef: eRef)
      javers.commit("a", e)

      e.intProperty = 30
      eRef.intProperty = 3
      javers.commit("a", eRef)
      javers.commit("a", e)

      e.intProperty = 33
      eRef.intProperty = 4
      javers.commit("a", eRef)
      javers.commit("a", e)

      when:
      def shadows = javers.findShadows(QueryBuilder.byInstance(e)
              .withScopeDeepPlus()
              .build()).collect{it.get()}

      then:
      shadows[0].entityRef.intProperty == 4
      shadows[1].entityRef.intProperty == 3
      shadows[2].entityRef.intProperty == 2
    }

    def "should query for deep Shadows of ValueObject in COMMIT_DEEP scope"() {
        given:
        def vo =  new DummyAddress(city: "London", networkAddress: new DummyNetworkAddress(address: "a"))
        def entity = new SnapshotEntity(id: 1, valueObjectRef: vo)
        javers.commit("a", entity)

        vo.city = "Paris"
        javers.commit("a", entity)

        vo.city = "Rome"
        javers.commit("a", entity)

        when:
        def shadows = javers.findShadows(QueryBuilder.byValueObjectId(1, SnapshotEntity, "valueObjectRef")
                .withScopeCommitDeep()
                .build()).collect{it.get()}

        then:
        shadows.size() == 3
        shadows.each {
            it instanceof DummyAddress
            it.networkAddress instanceof DummyNetworkAddress
        }

        shadows[0].city == "Rome"
        shadows[0].networkAddress.address == "a"

        shadows[1].city == "Paris"
        shadows[1].networkAddress.address == "a"

        shadows[2].city == "London"
        shadows[2].networkAddress.address == "a"
    }

    def "should query for Shadows of Entity in SHALLOW scope"() {
        given:
        def entity = new SnapshotEntity(id: 1, intProperty: 1)
        javers.commit("a", entity)
        entity.intProperty = 2
        javers.commit("a", entity)

        when:
        def shadows = javers.findShadows(QueryBuilder.byInstanceId(1, SnapshotEntity).build())

        then:
        shadows.size() == 2
        shadows.each {
            assert it.get() instanceof SnapshotEntity
            assert it.get().id == 1
        }

        shadows[0].commitMetadata.id.value() == "2.0"
        shadows[0].get().intProperty == 2

        shadows[1].commitMetadata.id.value() == "1.0"
        shadows[1].get().intProperty == 1
    }

    def "should query for all Entity Shadows in SHALLOW scope"() {
        given:
        javers.commit("a", new SnapshotEntity(id: 1))
        javers.commit("a", new SnapshotEntity(id: 2))
        javers.commit("a", new SnapshotEntity(id: 3))

        when:
        def shadows = javers.findShadows(QueryBuilder.byClass(SnapshotEntity)
                .build()).collect{it.get()}

        then:
        shadows.size() == 3
        shadows.each {
            assert it instanceof SnapshotEntity
        }
        shadows[0].id == 3
        shadows[1].id == 2
        shadows[2].id == 1
    }

    def "should query for Shadows by multiple classes"(){
        given:
        javers.commit("a", new SnapshotEntity(id:1))
        javers.commit("a", new OldEntity(id:1))
        javers.commit("a", new NewEntityWithTypeAlias(id:1))

        when:
        def shadows = javers.findShadows(QueryBuilder.byClass(NewEntity, NewEntityWithTypeAlias)
                .build()).collect{it.get()}

        then:
        shadows.size() == 2
        shadows[0] instanceof NewEntityWithTypeAlias
        shadows[1] instanceof OldEntity
    }

    def "should query for Shadows by ValueObject path"(){
        given:
        javers.commit("a", new SnapshotEntity(id: 1, valueObjectRef: new DummyAddress(city: "London")))
        javers.commit("a", new SnapshotEntity(id: 1, valueObjectRef: new DummyAddress(city: "Paris")))
        javers.commit("a", new SnapshotEntity(id: 1, arrayOfValueObjects: [new DummyAddress(city: "Paris")]))

        when:
        def shadows = javers.findShadows(QueryBuilder.byValueObject(SnapshotEntity, "valueObjectRef")
                .build()).collect{it.get()}

        then:
        shadows.size() == 2
        shadows[0].city == "Paris"
        shadows[1].city == "London"
    }

    def "should query for Shadows of Entity with Entity refs in COMMIT_DEEP scope"(){
        given:
        def ref1 = new SnapshotEntity(id: 5, intProperty: 5)
        javers.commit("a", new SnapshotEntity(id: 1, intProperty: 1, entityRef: ref1))

        def ref2 = new SnapshotEntity(id: 5, intProperty: 55)
        javers.commit("a", new SnapshotEntity(id: 1, intProperty: 2, entityRef: ref2))

        javers.commit("a", new SnapshotEntity(id: 1, intProperty: 3, entityRef: ref2))

        when:
        def shadows = javers.findShadows(byInstanceId(1, SnapshotEntity).withScopeCommitDeep()
                .build()).collect{it.get()}

        then:
        shadows.size() == 3
        shadows.each {
            assert it instanceof SnapshotEntity
            assert it.id == 1
        }
        shadows[0].intProperty == 3
        shadows[0].entityRef == ref2

        shadows[1].intProperty == 2
        shadows[1].entityRef == ref2

        shadows[2].intProperty == 1
        shadows[2].entityRef == ref1
    }

    def '''should return Entity Shadow with its child ValueObjects in SHALLOW scope
           (CHILD_VALUE_OBJECT should be enabled by default)'''(){
        given:
        def address = new DummyAddress(city: "London")
        def entity = new SnapshotEntity(id: 1, valueObjectRef: address, intProperty: 1)
        javers.commit("a", entity)

        address.city = "Paris"
        javers.commit("a", entity)

        address.networkAddress = new DummyNetworkAddress(address: "some")
        javers.commit("a", entity)

        address.networkAddress.address = "another"
        javers.commit("a", entity)

        entity.intProperty = 5
        javers.commit("a", entity)

        when:
        def query = QueryBuilder.byInstanceId(1, SnapshotEntity).build()
        def shadows = javers.findShadows(query).collect{it.get()}

        then:
        shadows.size() == 5
        shadows.each {
            assert it instanceof SnapshotEntity
            assert it.valueObjectRef instanceof DummyAddress
            assert it.id == 1
        }

        shadows[0].intProperty == 5
        shadows[0].valueObjectRef.city == "Paris"
        shadows[0].valueObjectRef.networkAddress.address == "another"

        shadows[1].intProperty == 1
        shadows[1].valueObjectRef.city == "Paris"
        shadows[1].valueObjectRef.networkAddress.address == "another"

        shadows[2].intProperty == 1
        shadows[2].valueObjectRef.city == "Paris"
        shadows[2].valueObjectRef.networkAddress.address == "some"

        shadows[3].intProperty == 1
        shadows[3].valueObjectRef.city == "Paris"
        !shadows[3].valueObjectRef.networkAddress

        shadows[4].intProperty == 1
        shadows[4].valueObjectRef.city == "London"
        !shadows[4].valueObjectRef.networkAddress

        query.stats().dbQueriesCount == 1
    }

    def "should query for Shadows with property filter using implicit CHILD_VALUE_OBJECT scope"() {
        given:
        def e = new SnapshotEntity(id: 1, valueObjectRef: new DummyAddress(city: "London"))
        javers.commit("author", e)

        e.intProperty = 5
        javers.commit("author", e)

        e.valueObjectRef = new DummyAddress(city: "Paris")
        javers.commit("author", e)

        e.intProperty = 6
        javers.commit("author", e)

        e.dob = LocalDate.now()
        javers.commit("author", e)

        when:
        def shadows = javers.findShadows(QueryBuilder.byClass(SnapshotEntity)
                .withChangedProperty("intProperty").build())

        then:
        with(shadows[0].get()){
            intProperty == 6
            valueObjectRef.city == "Paris"
        }

        with(shadows[1].get()){
            intProperty == 5
            valueObjectRef.city == "London"
        }
    }

    def "should run aggregate query when loading entity refs (using implicit CHILD_VALUE_OBJECT scope)"(){
      given:
      def ref = new SnapshotEntity(id: 2, valueObjectRef: new DummyAddress(city: "London"))
      javers.commit("a", ref)

      def entity = new SnapshotEntity(id: 1, entityRef: ref)
      javers.commit("a", entity)

      when:
      def query = QueryBuilder.byInstanceId(1, SnapshotEntity)
                .withScopeDeepPlus().build()
      def shadows = javers.findShadows(query).collect{it.get()}

      then:
      shadows.size() == 1
      shadows[0].entityRef.valueObjectRef.city == "London"

      query.stats().dbQueriesCount == 2
      query.stats().allSnapshotsCount == 3
    }

    def "should prefetch refs in DEEP_PLUS scope"(){
        given:
        def ref = new SnapshotEntity(id: 2)
        def entity = new SnapshotEntity(id: 1, entityRef: ref)

        4.times{
            ref.intProperty = it
            entity.intProperty = it
            javers.commit("a", ref)
            javers.commit("a", entity)
        }

        when:
        def query = QueryBuilder.byInstanceId(1, SnapshotEntity)
                .withScopeDeepPlus().build()
        def shadows = javers.findShadows(query).collect{it.get()}

        then:
        query.stats().dbQueriesCount == 2
        query.stats().allSnapshotsCount == 8
        query.stats().deepPlusSnapshotsCount == 4

        shadows[0].intProperty == 3
        shadows[0].entityRef.intProperty == 3
        shadows[1].intProperty == 2
        shadows[1].entityRef.intProperty == 2
        shadows[2].intProperty == 1
        shadows[2].entityRef.intProperty == 1
        shadows[3].intProperty == 0
        shadows[3].entityRef.intProperty == 0
    }

    def "should load latest Entity Shadow in DEEP_PLUS query"(){
        given:
        def ref = new SnapshotEntity(id: 2, intProperty: 1, valueObjectRef: new DummyAddress(city: "London"))
        javers.commit("a", ref)

        ref.intProperty = 5
        javers.commit("a", ref)

        def entity = new SnapshotEntity(id: 1, entityRef: ref, intProperty: 1)
        javers.commit("a", entity)

        entity.intProperty++
        javers.commit("a", entity)

        //noise
        ref.intProperty = 3
        javers.commit("a", ref)

        when:
        def shadows = javers.findShadows(QueryBuilder.byInstanceId(1, SnapshotEntity)
                .withScopeDeepPlus().build()).collect{it.get()}

        then:
        shadows.size() == 2

        shadows.each {
            assert it.entityRef.id == 2
            assert it.entityRef.intProperty == 5
            assert it.entityRef.valueObjectRef.city == "London"
        }
    }

    def "should stop filling gaps in a Shadow graph when DEEP_PLUS limit is exceeded"(){
        given:
        def ref1 = new SnapshotEntity(id: 2)
        def ref2 = new SnapshotEntity(id: 3)
        javers.commit("a", ref1)
        javers.commit("a", ref2)

        def entity = new SnapshotEntity(id: 1, listOfEntities: [ref1,ref2])
        javers.commit("a", entity)

        when:
        def shadows = javers.findShadows(QueryBuilder.byInstanceId(1, SnapshotEntity)
                .withScopeDeepPlus(1).build()).collect{it.get()}

        then:
        shadows.size() == 1

        shadows[0].listOfEntities.size() == 1
    }

    def "should load master snapshot even if child snapshots 'consumed' the snapshot limit"(){
        given:
        def a = new DummyAddress(city: "a")
        def e = new SnapshotEntity(id: 1, valueObjectRef:a)
        javers.commit("a", e)

        50.times {
            a.city = it
            javers.commit("a", e)
        }

        when:
        println "findSnapshots ..."
        def snapshots = javers.findSnapshots(byInstanceId(1, SnapshotEntity)
                .withChildValueObjects().limit(50).build())

        println "findShadows ..."
        def shadows = javers.findShadows(byInstanceId(1, SnapshotEntity)
                .limit(50).build()).collect{it.get()}

        then:
        snapshots.size() == 51
        snapshots.find {it -> it.globalId.typeName.endsWith('SnapshotEntity') && it.globalId.cdoId == 1}

        shadows.size() == 51
        shadows.each {
            it instanceof SnapshotEntity
            it.valueObjectRef instanceof DummyAddress
        }
    }
}