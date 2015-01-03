package org.javers.test.assertion

import org.javers.core.metamodel.property.PropertyAssert
import org.javers.core.metamodel.clazz.Entity

/**
 * @author Pawel Cierpiatka
 */
class EntityAssert {

    private Entity actual;

    static assertThat = {Entity entity ->
        return new EntityAssert(actual: entity)
    }

    PropertyAssert hasProperty(String propertyName) {
        def found = actual.properties.find { it.name == propertyName}
        assert found : "there is no such property ${propertyName}"
        PropertyAssert.assertThat(found)
    }
}

