package org.javers.core.diff

import org.javers.core.Javers
import org.javers.model.domain.Diff
import org.javers.test.builder.CategoryTestBuilder
import spock.lang.Specification
import org.javers.model.mapping.Category
import static org.fest.assertions.api.Assertions.assertThat
import static org.javers.core.JaversBuilder.javers
import static org.javers.test.builder.CategoryTestBuilder.category

/**
 *
 * @author bartosz walacik
 */
class DiffFactoryIntegrationTest extends Specification {
    def "should manage empty diff on big graphs"() {
        given:
        Category cat1 = category().deepWithChildNumber(5,5).build()
        Category cat2 = category().deepWithChildNumber(5,5).build()
        Javers javers = javers().registerEntity(Category).build()

        when:
        Diff diff = javers.compare("me", cat1, cat2)

        then:
        diff.changes.size() == 0
    }

    def "should manage full diff on very big graphs"() {
        given:
        Category cat1 = category().deepWithChildNumber(6,6).build()
        Category cat2 = category(-1).deepWithChildNumber(6,6).build()
        Javers javers = javers().registerEntity(Category).build()

        when:
        Diff diff = javers.compare("me", cat1, cat2)

        then:
        diff.changes.size() == 55987 * 2
    }


}
