package org.javers.test

import org.javers.model.domain.changeType.ReferenceChanged
import org.javers.model.mapping.Property


class ReferenceChangesAssert {

    def Collection<ReferenceChanged> actual;

    private ReferenceChangesAssert(Collection<ReferenceChanged> actual) {
        this.actual = actual;
    }

    def static ReferenceChangesAssert assertThat(Collection<ReferenceChanged> actual) {
        return new ReferenceChangesAssert(actual)
    }

    def ReferenceChangesAssert hasSize(int expected) {
        assert actual.size() == expected
        return this;
    }

    def ReferenceChangeAssert assertThatFirstChange() {
        return ReferenceChangeAssert.assertThat(actual.iterator().next())
    }

    static class ReferenceChangeAssert {

        def ReferenceChanged actual;

        private ReferenceChangeAssert(ReferenceChanged actual) {
            this.actual = actual;
        }

        def static ReferenceChangeAssert assertThat(ReferenceChanged actual) {
            return new ReferenceChangeAssert(actual)
        }

        def hasProperty(Property expected) {
            assert actual.property == expected
            return this
        }

        def hasCdoId(Object expected) {
            assert actual.globalCdoId.localCdoId == expected
            return this
        }

        def hasLeftReference(Object expected) {
            assert actual.leftReference == expected
            return this;
        }

        def hasRightReference(Object expected) {
            assert actual.rightReference == expected
            return this;
        }
    }
}
