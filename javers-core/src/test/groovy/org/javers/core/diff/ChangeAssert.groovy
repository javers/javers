package org.javers.core.diff

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.metamodel.object.InstanceId

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
        assert actual.affectedGlobalId instanceof InstanceId
        assert actual.affectedGlobalId.typeName == expected.name
        assert actual.affectedGlobalId.cdoId == expectedCdoId
        this
    }

    ChangeAssert hasEntityTypeOf(Class entityClass) {
        actual.affectedGlobalId.typeName == entityClass.name
        this
    }

    ChangeAssert hasCdoId(Object expectedCdoId) {
        actual.affectedGlobalId.cdoId == expectedCdoId
        this
    }

    ChangeAssert hasAffectedCdo(Object expectedAffectedCdo) {
        assert actual.affectedObject.get() == expectedAffectedCdo
        this
    }

    ChangeAssert hasPropertyName(String expectedPropertyName) {
        assert actual.propertyName == expectedPropertyName
        this
    }

}
