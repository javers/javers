package org.javers.core.diff

import org.javers.model.domain.Change
import org.javers.model.domain.Diff
import org.javers.model.domain.changeType.NewObject
import org.javers.model.domain.changeType.ObjectRemoved
import org.javers.model.domain.changeType.ReferenceAdded

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
    
    ChangeAssert hasCdoId(Object localCdoId) {
        actual.globalCdoId.localCdoId == localCdoId
        this
    }

    ChangeAssert hasEntityTypeOf(Class<?> entityClass) {
        actual.globalCdoId.entity.sourceClass == entityClass
        this
    }

    ChangeAssert hasParentEqualTo(Diff diff) {
        actual.parent == diff
        this
    }

    ChangeAssert hasAffectedCdo(Object expectedAffectedCdo) {
        actual.affectedCdo == expectedAffectedCdo
        this
    }
}
