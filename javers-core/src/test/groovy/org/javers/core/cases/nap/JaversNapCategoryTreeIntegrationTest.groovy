package org.javers.core.cases.nap

import org.javers.core.Javers
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.model.Category
import org.javers.test.builder.CategoryTestBuilder
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.diff.DiffAssert.assertThat

/**
 * <b>Use case</b> of our client multiprogram.pl, comparing large Category Trees
 *
 * @author bartosz walacik
 */
class JaversNapCategoryTreeIntegrationTest extends Specification {

    def "should check all nodes when calculating property changes"(){
        given:
        Category cat1 = CategoryTestBuilder.category().deepWithChildNumber(3, 3, "name").build()
        Category cat2 = CategoryTestBuilder.category().deepWithChildNumber(3, 3, "newName").build()
        Javers javers = javers().build()

        when:
        Diff diff = javers.compare(cat1, cat2)

        then:
        assertThat(diff).hasChanges(40).hasAllOfType(ValueChange)
    }

    def "should manage empty diff on big graphs"() {
        given:
        Category cat1 = CategoryTestBuilder.category().deepWithChildNumber(5,5).build()
        Category cat2 = CategoryTestBuilder.category().deepWithChildNumber(5,5).build()
        Javers javers = javers().build()

        when:
        Diff diff = javers.compare(cat1, cat2)

        then:
        diff.changes.size() == 0
    }

    def "should manage full diff on big graphs"() {
        given:
        Category cat1 = CategoryTestBuilder.category().deepWithChildNumber(5,5).build()
        Category cat2 = CategoryTestBuilder.category(-1).deepWithChildNumber(5,5).build()
        Javers javers = javers().build()

        when:
        Diff diff = javers.compare(cat1, cat2)

        then:
        assertThat(diff).has(3906 , NewObject)
        assertThat(diff).has(3906*2 , ValueChange)
        assertThat(diff).has(3905 , ReferenceChange)
        assertThat(diff).has(3906 , ObjectRemoved)
    }
}
