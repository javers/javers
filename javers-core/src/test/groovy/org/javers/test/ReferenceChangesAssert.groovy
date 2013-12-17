package org.javers.test

import org.javers.model.domain.GlobalCdoId
import org.javers.model.domain.changeType.ReferenceChanged
import org.javers.model.mapping.Property

class ReferenceChangesAssert {

    Collection<ReferenceChanged> actual

    static ReferenceChangesAssert assertThat(Collection<ReferenceChanged> actual) {
        new ReferenceChangesAssert(actual:actual)
    }

    ReferenceChangesAssert hasSize(int expected) {
        assert actual.size() == expected
        return this;
    }

    ReferenceChangeAssert assertThatFirstChange() {
        return ReferenceChangeAssert.assertThat(actual.iterator().next())
    }

    static class ReferenceChangeAssert {

        ReferenceChanged actual

        static ReferenceChangeAssert assertThat(ReferenceChanged actual) {
            new ReferenceChangeAssert(actual:actual)
        }

        ReferenceChanged hasProperty(Property expected) {
            assert actual.property == expected
            this
        }

        ReferenceChanged hasCdoId(Object expected) {
            assert actual.globalCdoId.localCdoId == expected
            this
        }

        ReferenceChanged hasLeftReference(Class expectedClass, Object expectedCdoId) {
            assert actual.leftReference.localCdoId == expectedCdoId
            assert actual.leftReference.entity.sourceClass == expectedClass
            this
        }

        ReferenceChanged hasRightReference(Class expectedClass, Object expectedCdoId) {
            assert actual.rightReference.localCdoId == expectedCdoId
            assert actual.rightReference.entity.sourceClass == expectedClass
            this
        }

        ReferenceChanged hasLeftReference(GlobalCdoId expected) {
            assert actual.leftReference == expected
            this
        }

        ReferenceChanged hasRightReference(GlobalCdoId expected) {
            assert actual.rightReference == expected
            this
        }
    }
}
