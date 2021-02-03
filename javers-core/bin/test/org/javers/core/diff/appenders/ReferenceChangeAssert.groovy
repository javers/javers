package org.javers.core.diff.appenders

import org.javers.core.diff.changetype.ReferenceChange

/**
 * @author bartosz walacik
 */
class ReferenceChangeAssert {

    ReferenceChange actual

    static ReferenceChangeAssert assertThat(ReferenceChange actual) {
        new ReferenceChangeAssert(actual:actual)
    }

    ReferenceChangeAssert hasPropertyName(String expected) {
        assert actual.propertyName == expected
        this
    }

    ReferenceChangeAssert hasLeftReference(Class expectedClass, Object expectedCdoId) {
        assert actual.left.cdoId == expectedCdoId
        assert actual.left.typeName == expectedClass.name
        this
    }

    ReferenceChangeAssert hasRightReference(Class expectedClass, Object expectedCdoId) {
        assert actual.right.cdoId == expectedCdoId
        assert actual.right.typeName == expectedClass.name
        this
    }

    ReferenceChangeAssert hasLeftReference(Object expected) {
        assert actual.left == expected
        this
    }

    ReferenceChangeAssert hasRightReference(Object expected) {
        assert actual.right == expected
        this
    }
    
    ReferenceChangeAssert hasLeftObject(Object expected) {
        assert expected == null && !actual.leftObject.isPresent() || expected == actual.leftObject.get()
        this
    }
    
    ReferenceChangeAssert hasRightObject(Object expected) {
        assert expected == null && !actual.rightObject.isPresent() || expected == actual.rightObject.get()
        this
    }
}
