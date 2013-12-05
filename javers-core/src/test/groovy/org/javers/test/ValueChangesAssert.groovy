package org.javers.test

import org.javers.model.domain.changeType.ValueChange
import org.javers.model.mapping.Property


class ValueChangesAssert {

    def Collection<ValueChange> actual;

    private ValueChangesAssert(Collection<ValueChange> actual) {
        this.actual = actual;
    }

    def static ValueChangesAssert assertThat(Collection<ValueChange> actual) {
        return new ValueChangesAssert(actual)
    }

    def ValueChangesAssert hasSize(int expected) {
        assert actual.size() == expected
        return this;
    }

    def ValueChangeAssert assertThatFirstChange() {
        return ValueChangeAssert.assertThat(actual.iterator().next())
    }

    static class ValueChangeAssert {

        def ValueChange actual;

        private ValueChangeAssert(ValueChange actual) {
            this.actual = actual;
        }

        def static ValueChangeAssert assertThat(ValueChange actual) {
            return new ValueChangeAssert(actual)
        }

        def hasProperty(Property expected) {
            assert actual.property == expected
            return this
        }

        def hasCdoId(Object expected) {
            assert actual.globalCdoId.localCdoId == expected
            return this
        }

        def hasLeftValue(Object expected) {
            assert actual.leftValue.value == expected
            return this;
        }

        def hasRightValue(Object expected) {
            assert actual.rightValue.value == expected
            return this;
        }

        def doesNotHaveLeftValue() {
            hasLeftValue(null)
        }
    }
}