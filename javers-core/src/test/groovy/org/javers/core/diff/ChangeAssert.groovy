package org.javers.core.diff

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.property.Property

/**
 * @author bartosz walacik
 */
class ChangeAssert {
    Change actual

    static ChangeAssert assertThat(Change actual) {
        new ChangeAssert(actual: actual)
    }

    ChangeAssert isNewObject() {
        actual.class == NewObject
        this
    }

    ChangeAssert isObjectRemoved() {
        actual.class == ObjectRemoved
        this
    }

    ChangeAssert hasInstanceId(Class expected, Object expectedCdoId) {
        assert actual.affectedCdoId instanceof InstanceId
        assert actual.affectedCdoId.cdoClass.clientsClass == expected
        assert actual.affectedCdoId.cdoId == expectedCdoId
        this
    }

    ChangeAssert hasEntityTypeOf(Class<?> entityClass) {
        actual.affectedCdoId.cdoClass.clientsClass == entityClass
        this
    }

    ChangeAssert hasCdoId(Object expectedCdoId) {
        actual.affectedCdoId.cdoId == expectedCdoId
        this
    }

    ChangeAssert hasAffectedCdo(Object expectedAffectedCdo) {
        assert actual.affectedCdo == expectedAffectedCdo
        this
    }

    ChangeAssert hasProperty(Property expected) {
        assert actual.property == expected
        this
    }

}
