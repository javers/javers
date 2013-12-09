package org.javers.test.assertion;

import org.fest.assertions.api.AbstractAssert;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.type.JaversType;

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

    public PropertyAssert hasJaversType(Class<? extends JaversType> expectedJaversType) {
        Assertions.assertThat( (Class) actual.getType().getClass()).isEqualTo((Class)expectedJaversType);
        return this;
    }

    public PropertyAssert hasName(String expected) {
        Assertions.assertThat(actual.getName()).isEqualTo(expected);
        return this;
    }

    public PropertyAssert hasJavaClass(Class expected) {
        Assertions.assertThat(actual.getType().getBaseJavaClass()).isEqualTo(expected);
        return this;
    }

    public PropertyAssert hasJavaType(Type expected) {
        Assertions.assertThat(actual.getType().getBaseJavaType()).isEqualTo(expected);
        return this;
    }

    public PropertyAssert looksLikeId() {
        Assertions.assertThat(actual.looksLikeId()).isTrue();
        return this;
    }
}
