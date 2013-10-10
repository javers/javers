package org.javers.model.mapping;

import org.fest.assertions.api.AbstractAssert;
import org.javers.model.mapping.type.JaversType;
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

    public PropertyAssert hasJaversType(Class<? extends JaversType> expectedJaversType) {
        System.out.println(actual.getType());
        System.out.println(actual.getType().getClass());

        Assertions.assertThat( (Class) actual.getType().getClass()).isEqualTo((Class)expectedJaversType);
        return this;
    }

    public PropertyAssert hasName(String expected) {
        Assertions.assertThat(actual.getName()).isEqualTo(expected);
        return this;
    }

    public PropertyAssert hasJavaType(Class expected) {
        Assertions.assertThat(actual.getType().getBaseJavaType()).isEqualTo(expected);
        return this;
    }

    public PropertyAssert isId() {
        Assertions.assertThat(actual.isId()).isTrue();
        return this;
    }
}
