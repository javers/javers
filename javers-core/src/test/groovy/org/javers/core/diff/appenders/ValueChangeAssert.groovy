package org.javers.core.diff.appenders

import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.object.ValueObjectId
import org.javers.core.metamodel.property.Property

class ValueChangeAssert {

    def ValueChange actual;

    private ValueChangeAssert(ValueChange actual) {
        this.actual = actual;
    }

    static ValueChangeAssert assertThat(ValueChange actual) {
        return new ValueChangeAssert(actual)
    }

    def hasValueObjectId(Class expected, def expectedOwnerId, String expectedFragment ){
        assert actual.affectedCdoId instanceof ValueObjectId
        assert actual.affectedCdoId.cdoClass.sourceClass == expected
        assert actual.affectedCdoId.ownerId == expectedOwnerId
        assert actual.affectedCdoId.fragment == expectedFragment
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
        assert actual.left == expected
        this;
    }

    def hasRightValue(Object expected) {
        assert actual.right == expected
        return this;
    }

    def haveLeftValueNull() {
        hasLeftValue(null)
    }
}