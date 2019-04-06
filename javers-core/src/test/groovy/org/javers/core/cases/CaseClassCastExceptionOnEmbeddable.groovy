package org.javers.core.cases

import org.javers.core.JaversBuilder
import spock.lang.Specification

import javax.persistence.EmbeddedId

class CaseClassCastExceptionOnEmbeddable extends Specification {

    class Entity {
        @EmbeddedId
        VO id
    }

    class VO {
        String a
        String b
    }

    def "should support VO as Entity Id"(){
      given:
      def javers = JaversBuilder.javers().build()

      when:
      println javers.getTypeMapping(Entity)
      println javers.getTypeMapping(VO)

      then:
      true
    }
}
