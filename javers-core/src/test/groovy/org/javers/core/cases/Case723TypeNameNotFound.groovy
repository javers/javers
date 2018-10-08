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

    @TypeName("not.existing.Entity")
    class Entity {
        @Id
        int identifier
        String text
        double number
    }

    def "should manage query for Value Object by concrete path"() {
        given:
        def repo = new InMemoryRepository()
        def oldJavers = JaversBuilder.javers().registerJaversRepository(repo).build()
        oldJavers.commit("author", new Entity(identifier: 1, text: "some", number: 2.3))

        when:
        // new javers instance - fresh TypeMapper state but using the same repository
        def newJavers = JaversBuilder.javers().registerJaversRepository(repo).build()
        def snapshot = newJavers.findSnapshots(QueryBuilder.byInstanceId(1, "not.existing.Entity").build()).get(0)

        then:
        snapshot.getPropertyValue("text") == "some"
        snapshot.getPropertyValue("number") == 2.3
    }

}
