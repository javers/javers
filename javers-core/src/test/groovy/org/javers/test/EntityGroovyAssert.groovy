package org.javers.test

import org.javers.model.mapping.Entity
import org.javers.test.assertion.Assertions

/**
 * @author Pawel Cierpiatka
 */
class EntityGroovyAssert {

    private Entity actual;

    def static assertThat(Entity entity) {
        return new EntityGroovyAssert(actual: entity)
    }

    PropertyAssert hasProperty(String propertyName) {
        def found = actual.getProperties().find { prop -> prop.name == propertyName}
        assert found : "there is no such property ${propertyName}"
        PropertyAssert.assertThat(found)
    }

    void hasNoProperty(String propertyName) {
        def found = actual.getProperties().find { prop -> prop.name == propertyName}
        assert found == null
    }

}
