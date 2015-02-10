package org.javers.core.metamodel.property

import org.javers.core.metamodel.clazz.Entity

/**
 * @author bartosz walacik
 */
class EntityAssert {
    private Entity actual
    
    private EntityAssert(Entity actual) {
        this.actual = actual
    }

     static EntityAssert assertThat(Entity actual) {
        new EntityAssert(actual)
    }

     EntityAssert hasIdProperty(String withName) {
        assert actual.idProperty.name == withName
        this
    }

     PropertyAssert hasProperty(String withName) {
        Property found = actual.getProperty(withName)
        assert found != null

        PropertyAssert.assertThat(found)
    }

    EntityAssert hasntGotProperty(String withName) {
        assert !actual.hasProperty(withName)
    }
}
