package org.javers.core.diff.appenders

import org.javers.core.metamodel.object.GlobalCdoId
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.metamodel.property.Property

/**
 * @author bartosz walacik
 */
class ReferenceChangeAssert {

    ReferenceChange actual

    static ReferenceChangeAssert assertThat(ReferenceChange actual) {
        new ReferenceChangeAssert(actual:actual)
    }

    ReferenceChangeAssert hasProperty(Property expected) {
        assert actual.property == expected
        this
    }

    ReferenceChangeAssert hasLeftReference(Class expectedClass, Object expectedCdoId) {
        assert actual.left.cdoId == expectedCdoId
        assert actual.left.cdoClass.sourceClass == expectedClass
        this
    }

    ReferenceChangeAssert hasRightReference(Class expectedClass, Object expectedCdoId) {
        assert actual.right.cdoId == expectedCdoId
        assert actual.right.cdoClass.sourceClass == expectedClass
        this
    }

    ReferenceChangeAssert hasLeftReference(GlobalCdoId expected) {
        assert actual.left == expected
        this
    }

    ReferenceChangeAssert hasRightReference(GlobalCdoId expected) {
        assert actual.right == expected
        this
    }
}
