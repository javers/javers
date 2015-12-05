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
        assert actual.affectedGlobalId instanceof ValueObjectId
        assert actual.affectedGlobalId.managedType.baseJavaClass == expected
        assert actual.affectedGlobalId.ownerId == expectedOwnerId
        assert actual.affectedGlobalId.fragment == expectedFragment
        this
    }

    def hasAffectedCdo(Object expectedAffectedCdo) {
        actual.affectedObject.get() == expectedAffectedCdo
        this
    }

    def hasProperty(String expected) {
        assert actual.propertyName == expected
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