package org.javers.test.assertion

import org.javers.core.metamodel.clazz.Entity

/**
 * @author Pawel Cierpiatka
 */
class CustomAssert {

    def static EntityAssert assertThat(Entity actual) {
        return EntityAssert.assertThat(actual)
    }

    def static CollectionAssert assertThat(Collection actual) {
        return CollectionAssert.assertThat(actual)
    }

}
