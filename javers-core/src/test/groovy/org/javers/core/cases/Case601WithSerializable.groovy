package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.type.EntityType
import spock.lang.Specification

class Case601WithSerializable extends Specification {

    abstract class MongoBaseModel<ID extends Serializable & Comparable<ID>> implements Comparable<MongoBaseModel<ID>> {

        protected int version

        @Id
        protected ID id

        int compareTo(MongoBaseModel<ID> o) {
            return id <=> o.id
        }
    }

    abstract class AbstractPermission extends MongoBaseModel<String> implements Serializable {
        protected String name
        protected String description
    }


    class Permission extends AbstractPermission {
        private Set<String> inclusions
        private Set<String> exclusions
    }

    class Role extends AbstractPermission {
        private Set<Permission> permissions
    }

    def "should infer type to EntityType when abstract class implements Serializable "(){
      given:
      def javers = JaversBuilder.javers().build()

      expect:
      javers.getTypeMapping(Role) instanceof EntityType
      javers.getTypeMapping(Permission) instanceof EntityType
      //javers.getTypeMapping(HashMap) instanceof MapType

    }
}
