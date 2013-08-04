package org.javers.model.mapping;

import org.fest.assertions.api.AbstractAssert;
import org.javers.test.assertion.Assertions;

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

    public PropertyAssert hasType(MetaType expected) {
        Assertions.assertThat(actual.getValueType()).isEqualTo(expected);
        return this;
    }
}
