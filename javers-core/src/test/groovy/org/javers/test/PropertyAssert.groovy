package org.javers.test

import org.javers.model.mapping.Property
import org.javers.model.mapping.type.JaversType

import java.lang.reflect.Type

/**
 * @author Pawel Cierpiatka
 */
class PropertyAssert {

    Property actual

    private PropertyAssert(Property actual) {
        this.actual = actual;
    }

    static PropertyAssert assertThat(Property actual) {
        return new PropertyAssert(actual)
    }

    PropertyAssert hasJaversType(Class<? extends JaversType> expectedJaversType) {
        assert actual.getType().getClass() == expectedJaversType
        return this
    }

    PropertyAssert hasJavaType(Type expected) {
        assert actual.getType().getBaseJavaType() == expected
        return this;
    }

    PropertyAssert isId() {
        assert actual.looksLikeId() == true
        return this;
    }
}
