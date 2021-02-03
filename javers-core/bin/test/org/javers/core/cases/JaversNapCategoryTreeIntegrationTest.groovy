package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ObjectRemoved
import org.javers.core.diff.changetype.ValueChange
import org.javers.test.builder.CategoryTestBuilder
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.diff.DiffAssert.assertThat

/**
 * <b>Use case</b> of our client multiprogram.pl, comparing large CategoryC Trees
 *
 * @author bartosz walacik
 */
class JaversNapCategoryTreeIntegrationTest extends Specification {

    def "should check all nodes when calculating property changes"(){
        given:
        def cat1 = CategoryTestBuilder.category().deepWithChildNumber(3, 3, "name").build()
        def cat2 = CategoryTestBuilder.category().deepWithChildNumber(3, 3, "newName").build()
        Javers javers = javers().build()

        when:
        Diff diff = javers.compare(cat1, cat2)

        then:
        assertThat(diff).hasChanges(40).hasAllOfType(ValueChange)
    }

    def "should manage empty diff on big graphs"() {
        given:
        def cat1 = CategoryTestBuilder.category().deepWithChildNumber(5,5).build()
        def cat2 = CategoryTestBuilder.category().deepWithChildNumber(5,5).build()
        Javers javers = javers().build()

        when:
        Diff diff = javers.compare(cat1, cat2)

        then:
        !diff.changes
    }

    def "should manage full diff on big graphs"() {
        given:
        def cat1 = CategoryTestBuilder.category().deepWithChildNumber(5,5).build()
        def cat2 = CategoryTestBuilder.category(-1).deepWithChildNumber(5,5).build()
        Javers javers = javers().build()

        when:
        Diff diff = javers.compare(cat1, cat2)

        then:
        diff.getChangesByType(NewObject).size() == 3906
        diff.getChangesByType(ObjectRemoved).size() == 3906
        diff.getChanges().size() == 3906 * 2
    }
}
