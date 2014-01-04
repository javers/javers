package org.javers.core.diff

import de.danielbechler.diff.ObjectDiffer
import de.danielbechler.diff.ObjectDifferFactory
import de.danielbechler.diff.visitor.PrintingVisitor
import org.javers.core.Javers
import org.javers.model.domain.Diff
import org.javers.model.domain.changeType.ValueChange
import org.javers.model.mapping.Category
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.diff.DiffAssert.assertThat
import static org.javers.test.builder.CategoryTestBuilder.category

/**
 *
 * @author bartosz walacik
 */
class DiffFactoryIntegrationTest extends Specification {

    def "should check all nodes when calculating property changes"(){
        given:
        Category cat1 = category().deepWithChildNumber(3, 3, "name").build()
        Category cat2 = category().deepWithChildNumber(3, 3, "newName").build()
        Javers javers = javers().registerEntity(Category).build()

        when:
        Diff diff = javers.compare("me", cat1, cat2)

        then:
        assertThat(diff).hasSize(40).hasAllOfType(ValueChange)
    }

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

    def "should manage full diff on big graphs"() {
        given:
        Category cat1 = category().deepWithChildNumber(5,5).build()
        Category cat2 = category(-1).deepWithChildNumber(5,5).build()
        Javers javers = javers().registerEntity(Category).build()

        when:
        Diff diff = javers.compare("me", cat1, cat2)

        then:
        assertThat(diff).hasSize(3906 * 2)
    }
}
