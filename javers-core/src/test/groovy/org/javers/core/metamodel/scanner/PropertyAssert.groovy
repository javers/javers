package org.javers.core.metamodel.scanner

import org.javers.core.metamodel.property.Property

import java.lang.reflect.Type

/**
 * @author bartosz walacik
 */
class PropertyAssert {
    private Property actual

    private PropertyAssert(Property actual) {
        this.actual = actual
    }

    static PropertyAssert assertThat(Property actual) {
        new PropertyAssert(actual)
    }

    PropertyAssert hasJavaType(Type expected) {
        assert actual.genericType == expected
        this
    }

    PropertyAssert isTransient() {
        assert actual.hasTransientAnn
        this
    }

    PropertyAssert isIncluded() {
        assert actual.hasIncludedAnn
        this
    }

    PropertyAssert looksLikeId() {
        assert actual.looksLikeId()
        this
    }

    PropertyAssert hasValue(Object target, Object expectedValue){
        assert actual.get(target) == expectedValue
        this
    }
}
