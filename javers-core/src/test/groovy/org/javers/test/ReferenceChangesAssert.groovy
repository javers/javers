package org.javers.test

import org.javers.model.domain.GlobalCdoId
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
            new ReferenceChangeAssert(actual)
        }

        def hasProperty(Property expected) {
            assert actual.property == expected
            this
        }

        def hasCdoId(Object expected) {
            assert actual.globalCdoId.localCdoId == expected
            this
        }

        def hasLeftReference(Class expectedClass, Object expectedCdoId) {
            assert actual.leftReference.localCdoId == expectedCdoId
            assert actual.leftReference.entity.sourceClass == expectedClass
            this
        }

        def hasRightReference(Class expectedClass, Object expectedCdoId) {
            assert actual.rightReference.localCdoId == expectedCdoId
            assert actual.rightReference.entity.sourceClass == expectedClass
            this
        }

        def hasLeftReference(GlobalCdoId expected) {
            assert actual.leftReference == expected
            this
        }

        def hasRightReference(GlobalCdoId expected) {
            assert actual.rightReference == expected
            this
        }
    }
}
