package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.MappingStyle
import org.javers.core.metamodel.clazz.EntityDefinition
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class Case640InterfaceGettersInheritance extends Specification {

    interface WithId {
        String getId()
        void setId(String id)
    }

    interface MyObject extends WithId, Serializable {
        //getId() should be inherited
    }

    def "should ... "(){
      given:
      Javers javers = JaversBuilder.javers()
              .withMappingStyle(MappingStyle.BEAN)
              .registerEntity(new EntityDefinition(MyObject.class, "id"))
              .build()

      expect:
      javers.getTypeMapping(MyObject).idProperty.name == "id"
    }
}
