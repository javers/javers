package org.javers.core.diff.appenders

import org.javers.core.diff.ChangeAssert
import org.javers.core.diff.changetype.ValueChange
import org.javers.model.domain.GlobalCdoId
import org.javers.model.domain.ValueObjectId
import org.javers.model.mapping.Property

class ValueChangeAssert {

    def ValueChange actual;

    private ValueChangeAssert(ValueChange actual) {
        this.actual = actual;
    }

    static ValueChangeAssert assertThat(ValueChange actual) {
        return new ValueChangeAssert(actual)
    }

    def hasValueObjectId(Class expected, GlobalCdoId expectedOwnerId, String expectedFragment ){
        assert actual.globalCdoId instanceof ValueObjectId
        assert actual.globalCdoId.cdoClass.sourceClass == expected
        assert actual.globalCdoId.ownerId == expectedOwnerId
        assert actual.globalCdoId.fragment == expectedFragment
        this
    }

    def hasAffectedCdo(Object expectedAffectedCdo) {
        actual.affectedCdo == expectedAffectedCdo
        this
    }

    def hasProperty(Property expected) {
        assert actual.property == expected
        this
    }

    def hasLeftValue(Object expected) {
        assert actual.leftValue.value == expected
        this;
    }

    def hasRightValue(Object expected) {
        assert actual.rightValue.value == expected
        return this;
    }

    def haveLeftValueNull() {
        hasLeftValue(null)
    }
}