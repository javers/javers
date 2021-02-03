package org.javers.core

import org.javers.core.examples.typeNames.*
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.core.metamodel.object.InstanceId
import org.javers.repository.inmemory.InMemoryRepository
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.repository.jql.QueryBuilder.byInstanceId

class TypeRefactorE2ETest extends Specification {

    def "should manage Entity class name refactor when old and new class uses @TypeName"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        javers.commit("author", new OldEntityWithTypeAlias(id: 1, val: 5))
        javers.commit("author", new NewEntityWithTypeAlias(id: 1, val: 15))

        def changes = javers.findChanges(byInstanceId(1, NewEntityWithTypeAlias).build())

        then:
        changes.size() == 1
        with(changes.find { it.propertyName == "val" }) {
            assert left == 5
            assert right == 15
        }
    }

    def "should manage ValueObject class name refactor without TypeName when querying by owning Instance"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        javers.commit("author", new EntityWithRefactoredValueObject(id: 1, value: new OldValueObject(5, 5)))
        javers.commit("author", new EntityWithRefactoredValueObject(id: 1, value: new NewValueObject(5, 10)))
        javers.commit("author", new EntityWithRefactoredValueObject(id: 1, value: new NewValueObject(5, 15)))

        def changes = javers.findChanges(QueryBuilder.byValueObject(EntityWithRefactoredValueObject, "value").build())

        then:
        changes.size() == 3

        changes[0].propertyName == "newField"
        changes[0].left == 10
        changes[0].right == 15

        changes[1].propertyName == "oldField"
        changes[1].left == 5  //removed properties are treated as nulls
        changes[1].right == 0

        changes[2].propertyName == "newField"
        changes[2].left == 0  //removed properties are treated as nulls
        changes[2].right == 10
    }

    def "should manage ValueObject class name refactor when querying using new class with @TypeName retrofitted to old class name"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        javers.commit("author", new EntityWithRefactoredValueObject(id: 1, value: new OldValueObject(5, 10)))
        javers.commit("author", new EntityWithRefactoredValueObject(id: 1, value: new NewNamedValueObject(6, 10)))

        def changes = javers.findChanges(QueryBuilder.byClass(NewNamedValueObject).build())

        then:
        changes.size() == 3
        with(changes.find { it.propertyName == "newField" }) {
            assert left == 0 //removed properties are treated as nulls
            assert right == 10
        }
        with(changes.find { it.propertyName == "someValue" }) {
            assert left == 5
            assert right == 6
        }
    }

    def "should treat refactored VOs as different versions of the same client's domain object"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit('author', new EntityWithRefactoredValueObject(id: 1, value: new OldValueObject(5, 5)))
        javers.commit('author', new EntityWithRefactoredValueObject(id: 1, value: new NewValueObject(5, 10)))

        when:
        def snapshots = javers.findSnapshots(QueryBuilder.byValueObject(EntityWithRefactoredValueObject, 'value').build())

        then:
        snapshots.version == [2, 1]
    }

    @Unroll
    def "should manage Entity class name refactor when querying using new class with @TypeName retrofitted to old class name"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        javers.commit("author", new OldEntity(id: 1, value: 5))
        javers.commit("author", new NewEntity(id: 1, value: 15))

        def changes = javers.findChanges(byInstanceId(1, type).build())

        then:
        changes.size() == 1
        with(changes.find { it.propertyName == "value" }) {
            assert left == 5
            assert right == 15
        }

        where:
        type << [NewEntity, "org.javers.core.examples.typeNames.OldEntity"]
    }

    class Entity {
        @Id int id
        Ref ref
    }

    class Ref {
        int id
    }

    def "should manage Entity to Value type refactor when reading Snapshots and Shadows"(){
        given:
        def repo = new InMemoryRepository()

        def javers = JaversBuilder.javers()
                .registerJaversRepository(repo)
                .registerEntity(new EntityDefinition(Ref, "id"))
                .build()

        javers.commit("a", new Entity(id:1, ref:new Ref(id:2)))

        javers = JaversBuilder.javers()
                .registerJaversRepository(repo)
                .registerValue(Ref)
                .build()

        javers.commit("a", new Entity(id:1, ref:new Ref(id:2)))

        when: "Snapshots read"
        def snapshots = javers.findSnapshots(QueryBuilder.anyDomainObject().build())

        then:
        snapshots.size() == 3

        with(snapshots.find{it.globalId.cdoId == 2}) {
            assert it.globalId instanceof InstanceId
            assert it.globalId.typeName.endsWith('TypeRefactorE2ETest$Ref')
        }

        snapshots.findAll{it.globalId.cdoId == 1}.each {
            assert it.getPropertyValue("ref") instanceof Ref
        }
    }
}