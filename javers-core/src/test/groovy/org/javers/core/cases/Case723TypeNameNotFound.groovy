package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName
import org.javers.repository.inmemory.InMemoryRepository
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * see https://github.com/javers/javers/issues/723
 */
class Case723TypeNameNotFound extends Specification {

    @TypeName("removed.Entity")
    class RemovedEntity {
        @Id
        int identifier
        String text
        double number
    }

    @TypeName("owner.Entity")
    class OldOwnerEntity {
        @Id
        String id
        String name
        RemovedEntity child
    }

    @TypeName("owner.Entity")
    class NewOwnerEntity {
        @Id
        String id
        String name
    }

    def "should manage query for removed entity"() {
        given:
        def repo = new InMemoryRepository()
        def oldJavers = JaversBuilder.javers().registerJaversRepository(repo).build()
        oldJavers.commit("author", new RemovedEntity(identifier: 1, text: "some", number: 2.3))

        when:
        // new javers instance - fresh TypeMapper state but using the same repository
        def newJavers = JaversBuilder.javers().registerJaversRepository(repo).build()
        def snapshot = newJavers.findSnapshots(QueryBuilder.byInstanceId(1, "removed.Entity").build()).get(0)

        then:
        snapshot.getPropertyValue("text") == "some"
        snapshot.getPropertyValue("number") == 2.3
    }

    def "should manage query for an entity used to have a removed entity as child"() {
        given:
        def repo = new InMemoryRepository()
        def oldJavers = JaversBuilder.javers().registerJaversRepository(repo).build()
        RemovedEntity removedEntity = new RemovedEntity(identifier: 1, text: "some", number: 2.3)
        OldOwnerEntity ownerEntity = new OldOwnerEntity(id: "rambo", name: "some old entity", child: removedEntity)
        oldJavers.commit("author", ownerEntity)

        when:
        // new javers instance - fresh TypeMapper state but using the same repository
        def newJavers = JaversBuilder.javers().registerJaversRepository(repo).build()
        def snapshot = newJavers.findSnapshots(QueryBuilder.byInstanceId("rambo", NewOwnerEntity.class).build()).get(0)

        then:
        snapshot.getPropertyValue("id") == "rambo"
        snapshot.getPropertyValue("name") == "some old entity"
    }

}
