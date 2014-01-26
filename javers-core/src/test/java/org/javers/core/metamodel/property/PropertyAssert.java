package org.javers.core.metamodel.property;

import org.fest.assertions.api.AbstractAssert;
import org.javers.test.assertion.Assertions;

import java.lang.reflect.Type;

/**
 * @author bartosz walacik
 */
public class PropertyAssert extends AbstractAssert<PropertyAssert, Property> {

    private PropertyAssert(Property actual) {
        super(actual, PropertyAssert.class);
    }

    public static PropertyAssert assertThat(Property actual) {
        return new PropertyAssert(actual);
    }

    public PropertyAssert hasJavaType(Type expected) {
        Assertions.assertThat(actual.getGenericType()).isEqualTo(expected);
        return this;
    }
}
