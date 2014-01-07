package org.javers.core.diff.appenders

import org.javers.model.domain.GlobalCdoId
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.model.mapping.Property

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
        assert actual.leftReference.cdoId == expectedCdoId
        assert actual.leftReference.entity.sourceClass == expectedClass
        this
    }

    ReferenceChangeAssert hasRightReference(Class expectedClass, Object expectedCdoId) {
        assert actual.rightReference.cdoId == expectedCdoId
        assert actual.rightReference.entity.sourceClass == expectedClass
        this
    }

    ReferenceChangeAssert hasLeftReference(GlobalCdoId expected) {
        assert actual.leftReference == expected
        this
    }

    ReferenceChangeAssert hasRightReference(GlobalCdoId expected) {
        assert actual.rightReference == expected
        this
    }
}
