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
        int id
        String value
    }


    def "should manage query for Value Object by concrete path"(){
      given:
      def repo = new InMemoryRepository()
      def javers = JaversBuilder.javers().registerJaversRepository(repo) .build()

      when:
      javers.commit("author", new Entity(id:1, value: "some"))

      // new javers instance - fresh TypeMapper state
      javers = JaversBuilder.javers().registerJaversRepository(repo) .build()

      def snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(1, "not.existing.Entity").build()).get(0)

      then:
      snapshot.getPropertyValue("value") == "some"
    }
}
