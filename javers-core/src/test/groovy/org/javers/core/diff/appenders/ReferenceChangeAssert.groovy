package org.javers.core.diff.appenders

import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.metamodel.object.GlobalId
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
        assert actual.left.entity.sourceClass == expectedClass
        this
    }

    ReferenceChangeAssert hasRightReference(Class expectedClass, Object expectedCdoId) {
        assert actual.right.cdoId == expectedCdoId
        assert actual.right.entity.sourceClass == expectedClass
        this
    }

    ReferenceChangeAssert hasLeftReference(GlobalId expected) {
        assert actual.left == expected
        this
    }

    ReferenceChangeAssert hasRightReference(GlobalId expected) {
        assert actual.right == expected
        this
    }
}
