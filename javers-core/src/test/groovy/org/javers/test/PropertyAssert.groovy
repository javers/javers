package org.javers.test

import org.javers.model.mapping.Property
import org.javers.model.mapping.type.JaversType

/**
 * @author Pawel Cierpiatka
 */
class PropertyAssert {

    def Property actual;

    private PropertyAssert(Property actual) {
        this.actual = actual;
    }

    def static PropertyAssert assertThat(Property actual) {
        return new PropertyAssert(actual)
    }

    def PropertyAssert hasJaversType(Class<? extends JaversType> expectedJaversType) {
        assert actual.getType().getClass() == expectedJaversType
        return this
    }

    def PropertyAssert hasJavaType(Class expected) {
        assert actual.getType().getBaseJavaType() == expected
        return this;
    }

    def PropertyAssert isId() {
        assert actual.isId() == true;
        return this;
    }
}
