package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.type.EntityType
import org.javers.core.metamodel.type.TokenType
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
    }

    class Permission extends AbstractPermission {
        private Set<String> inclusions
    }

    class Role extends AbstractPermission {
        private Set<Permission> permissions
    }

    def "should resolve type tokens in Id property"(){
      given:
      def javers = JaversBuilder.javers().build()

      def roleType = javers.getTypeMapping(Role)
      def permissionType = javers.getTypeMapping(Permission)
      def mongoBaseModelType = javers.getTypeMapping(MongoBaseModel)

      expect:
      [roleType, permissionType, mongoBaseModelType].each {
          assert it instanceof EntityType
      }
      roleType.idProperty.type.baseJavaClass == String
      permissionType.idProperty.type.baseJavaClass == String
      mongoBaseModelType.idProperty.type instanceof TokenType
    }
}
