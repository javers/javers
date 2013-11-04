package org.javers.test

import org.javers.model.mapping.Entity

/**
 * @author Pawel Cierpiatka
 */
class CustomAssert {

    def static EntityGroovyAssert assertThat(Entity actual) {
        return EntityGroovyAssert.assertThat(actual)
    }

    def static CollectionAssert assertThat(Collection actual) {
        return CollectionAssert.assertThat(actual)
    }

}
