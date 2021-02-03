package org.javers.core.cases

import com.fasterxml.jackson.databind.ObjectMapper
import org.javers.core.JaversBuilder
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class JacksonSerializationTest extends Specification {

    def "should not fail when converting Diff to JSON using Jackson"(){
      given:
      def javers = JaversBuilder.javers().build()
      def left = new SnapshotEntity(id:1, intProperty: 1)
      def right = new SnapshotEntity(id:1, intProperty: 2)
      def mapper = new ObjectMapper()
      javers.commit("a", left)
      javers.commit("a", right)

      when:
      def diff = javers.compare(left, right)
      def json = mapper.writeValueAsString(diff)

      then:
      assert json

      when:
      def changes = javers.findChanges(QueryBuilder.byInstanceId(1,SnapshotEntity).build())
      json = mapper.writeValueAsString(changes)

      then:
      assert json
    }
}
