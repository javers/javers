package org.javers.test.assertion;

import org.fest.assertions.api.AbstractAssert;
import org.javers.model.Entity;

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

    @SuppressWarnings("unchecked")
    public EntityAssert hasSourceClass(Class<?> sourceClass) {
        Assertions.assertThat(actual.getSourceClass()).isSameAs((Class) sourceClass);
        return this;
    }
}
