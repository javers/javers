package org.javers.test

/**
 * @author Pawel Cierpiatka
 */
class CollectionAssert {

    def Collection actual

    private CollectionAssert(Collection actual) {
        this.actual = actual
    }

    def static CollectionAssert assertThat(Collection actual) {
        return new CollectionAssert(actual)
    }

    def CollectionAssert hasSize(int expected) {
        assert actual.size() == expected;
        return this;
    }
}
