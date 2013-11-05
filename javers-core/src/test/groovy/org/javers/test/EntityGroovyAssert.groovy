package org.javers.test

import org.javers.model.mapping.Entity

/**
 * @author Pawel Cierpiatka
 */
class EntityGroovyAssert {

    private Entity actual;

    def static assertThat(Entity entity) {
        return new EntityGroovyAssert(actual: entity)
    }

    def PropertyAssert hasProperty(String propertyName) {
        def found = actual.getProperties().find { prop -> prop.name == propertyName}
        assert found : "there is no such property ${propertyName}"
        return PropertyAssert.assertThat(found);;
    }
}
