package org.javers.test.assertion;

import org.fest.assertions.api.AbstractAssert;
import org.javers.core.metamodel.property.PropertyAssert;
import org.javers.model.mapping.Entity;
import org.javers.core.metamodel.property.Property;

/**
 *
 * @author Adam Dubiel <adam.dubiel@gmail.com>
 */
public class EntityAssert extends AbstractAssert<EntityAssert, Entity> {

    private EntityAssert(Entity actual) {
        super(actual, EntityAssert.class);
    }

    public static EntityAssert assertThat(Entity actual) {
        return new EntityAssert(actual);
    }

    public EntityAssert hasIdProperty(String withName) {
       actual.getIdProperty().getName().equals(withName);
       return this;
    }

    public PropertyAssert hasProperty(String withName) {
        Property found = actual.getProperty(withName);
        Assertions.assertThat(found).isNotNull();

        return PropertyAssert.assertThat(found);
    }

    public PropertyAssert hasntGotProperty(String withName) {
        Property found = actual.getProperty(withName);
        Assertions.assertThat(found).isNull();

        return PropertyAssert.assertThat(found);
    }

    public EntityAssert hasSourceClass(Class<?> sourceClass) {
        Assertions.assertThat(actual.getSourceClass()).isSameAs((Class) sourceClass);
        return this;
    }
}
