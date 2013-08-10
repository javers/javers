package org.javers.model.mapping;

import org.fest.assertions.api.AbstractAssert;
import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.MetaType;
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

    public PropertyAssert hasJaversType(Class<? extends JaversType> expectedClass) {
        Assertions.assertThat( expectedClass.isAssignableFrom(actual.getType().getClass())).isTrue();
        return this;
    }

    public PropertyAssert hasJavaType(Type expected) {
        Assertions.assertThat(actual.getType().getJavaType()).isEqualTo(expected);
        return this;
    }
}
