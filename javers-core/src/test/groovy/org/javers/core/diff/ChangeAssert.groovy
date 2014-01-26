package org.javers.core.diff

import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceAdded
import org.javers.model.domain.InstanceId
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

    ChangeAssert isReferenceAdded() {
        actual.class == ReferenceAdded
        this
    }

    ChangeAssert hasInstanceId(Class expected, Object expectedCdoId) {
        assert actual.globalCdoId instanceof InstanceId
        assert actual.globalCdoId.cdoClass.sourceClass == expected
        assert actual.globalCdoId.cdoId == expectedCdoId
        this
    }

    ChangeAssert hasEntityTypeOf(Class<?> entityClass) {
        actual.globalCdoId.cdoClass.sourceClass == entityClass
        this
    }

    ChangeAssert hasCdoId(Object expectedCdoId) {
        actual.globalCdoId.cdoId == expectedCdoId
        this
    }

    ChangeAssert hasAffectedCdo(Object expectedAffectedCdo) {
        actual.affectedCdo == expectedAffectedCdo
        this
    }

    ChangeAssert hasProperty(Property expected) {
        assert actual.property == expected
        this
    }

}
