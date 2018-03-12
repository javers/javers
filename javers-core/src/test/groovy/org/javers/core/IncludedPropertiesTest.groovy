package org.javers.core

import org.javers.core.metamodel.clazz.EntityDefinitionBuilder
import org.javers.core.metamodel.clazz.JaversEntity
import org.javers.core.model.DummyUser
import spock.lang.Specification

class IncludedPropertiesTest extends Specification {

    def "should be able to commit when a class is configured using ClientsClassConfiguration with includedProperties"() {
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
        def javers = JaversBuilder.javers().registerEntity(EntityDefinitionBuilder.entityDefinition(DummyClassWithCyclicReference.class).withIdPropertyName("id").withIncludedProperties(["id", "child"]).build()).build()
        def entityInstance = new DummyClassWithCyclicReference(id: 1)
        entityInstance.child = new DummyClassWithCyclicReference2(parent: entityInstance);
        javers.commit("user", entityInstance)

        then:
        noExceptionThrown()
    }
}

class DummyClassWithCyclicReference2 {
    DummyClassWithCyclicReference parent
}

class DummyClassWithCyclicReference extends JaversEntity {
    DummyClassWithCyclicReference2 child;
}
