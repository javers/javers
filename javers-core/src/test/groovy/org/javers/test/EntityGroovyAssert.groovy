package org.javers.test

import org.javers.core.metamodel.property.PropertyAssert
import org.javers.core.metamodel.property.Entity

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
