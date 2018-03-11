package org.javers.core

import org.javers.core.metamodel.clazz.EntityDefinitionBuilder
import org.javers.core.model.DummyClassWithCyclicReference
import org.javers.core.model.DummyClassWithCyclicReference2
import org.javers.core.model.DummyUser
import spock.lang.Specification

class IncludedPropertiesTest extends Specification {

    def "Javers should be able to build when a class is configured using ClientsClassConfiguration with includedProperties"() {
        when:
        def javers = JaversBuilder.javers().registerEntity(entityDefinition).build()
        javers.commit("user", entityInstance)

        then:
        noExceptionThrown()

        where:
        entityDefinition << [EntityDefinitionBuilder.entityDefinition(DummyUser.class).withIdPropertyName("name").withIncludedProperties(["name", "surname"]).build()]
        entityInstance << [new DummyUser("Johny", "Doe").withAddress("Melbourne")]
    }

    def "should handle cyclical objects" () {
        when:
        def javers = JaversBuilder.javers().registerEntity(EntityDefinitionBuilder.entityDefinition(DummyClassWithCyclicReference.class).withIdPropertyName("name").withIncludedProperties(["name", "child"]).build()).build()
        def entityInstance = new DummyClassWithCyclicReference(name: "Johny")
        entityInstance.child = new DummyClassWithCyclicReference2(parent: entityInstance);
        javers.commit("user", entityInstance)

        then:
        noExceptionThrown()
    }
}
