package org.javers.core

import org.javers.core.metamodel.clazz.EntityDefinitionBuilder
import org.javers.core.model.DummyClassWithCyclicReference
import org.javers.core.model.DummyUser
import spock.lang.Specification

class IncludedPropertiesTest extends Specification {

    def "Javers should be able to build when a class is configured using ClientsClassConfiguration with includedProperties"() {
        when:
        def javers = JaversBuilder.javers().registerEntity(entityDefinition).build()
        javers.intial(entityInstance)

        then:
        noExceptionThrown()

        where:
        entityDefinition << [EntityDefinitionBuilder.entityDefinition(DummyUser.class).withIdPropertyName("name").withIncludedProperties(["name", "surname"]).build(),
                             EntityDefinitionBuilder.entityDefinition(DummyClassWithCyclicReference.class).withIdPropertyName("name").withIncludedProperties(["name", "parent"]).build()]
        entityInstance << [new DummyUser("Jonny", "Doe").withAddress("Melbourne"),
                           new DummyClassWithCyclicReference(name: "Jonny", parent: new DummyClassWithCyclicReference(name: "Jonny2"))]
    }
}
