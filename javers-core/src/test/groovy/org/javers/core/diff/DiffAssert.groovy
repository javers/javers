package org.javers.core.diff

import org.javers.model.domain.Change
import org.javers.model.domain.Diff

/**
 * @author bartosz walacik
 */
class DiffAssert {
    Diff actual

    static DiffAssert assertThat(Diff actual) {
        new DiffAssert(actual: actual)
    }

    DiffAssert hasSize(int expectedSize) {
        assert actual.changes.size() == expectedSize
        this
    }

    DiffAssert hasAllOfType(Class<? extends Change> expectedClass) {
        actual.changes.each { assert it.class == expectedClass}
        this
    }

    ChangeAssert getChangeAtIndex(int index) {
        assert actual.changes.size() > index
        ChangeAssert.assertThat(actual.changes.get(index))
    }
}
