package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName
import org.javers.core.metamodel.object.ValueObjectId
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
        VO ref
    }

    @TypeName("not.existing.ValueObject")
    class VO {
        String value
    }

    def "should load Snapshots of removed Classes"(){
      given:
      def repo = new InMemoryRepository()
      def javers = JaversBuilder.javers().registerJaversRepository(repo) .build()

      when:
      javers.commit("author", new Entity(id:1, value: "some", ref: new VO(value: "aaa")))

      // new javers instance - fresh TypeMapper state
      javers = JaversBuilder.javers().registerJaversRepository(repo) .build()

      def snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(1, "not.existing.Entity").build()).get(0)

      then:
      snapshot.getPropertyValue("value") == "some"
      snapshot.getPropertyValue("id") == 1
      snapshot.getPropertyValue("ref") instanceof ValueObjectId
      snapshot.getPropertyValue("ref").value() == "not.existing.Entity/1#ref"
      snapshot.globalId.value() == "not.existing.Entity/1"

      when:
      snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(1, "not.existing.Entity")
                        .withChildValueObjects().build())
                        .find{it.globalId.value() == "not.existing.Entity/1#ref"}

      then:
      snapshot.getPropertyValue("value") == "aaa"
    }
}
