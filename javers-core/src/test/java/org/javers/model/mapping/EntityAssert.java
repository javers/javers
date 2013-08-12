package org.javers.model.mapping;

import org.fest.assertions.api.AbstractAssert;
import org.javers.test.assertion.Assertions;

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
