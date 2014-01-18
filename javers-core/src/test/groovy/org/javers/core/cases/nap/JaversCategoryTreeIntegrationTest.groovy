package org.javers.core.cases.nap

import org.javers.core.Javers
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.ValueChange
import spock.lang.Ignore
import spock.lang.Specification
import static org.javers.core.JaversBuilder.javers
import static org.javers.core.diff.DiffAssert.assertThat

import org.javers.core.model.Category

/**
 * <b>Use case</b> of our client multiprogram.pl, comparing large Category Trees
 *
 * @author bartosz walacik
 */
class JaversCategoryTreeIntegrationTest extends Specification {

    def "should check all nodes when calculating property changes"(){
        given:
        Category cat1 = CategoryTestBuilder.category().deepWithChildNumber(3, 3, "name").build()
        Category cat2 = CategoryTestBuilder.category().deepWithChildNumber(3, 3, "newName").build()
        Javers javers = javers().registerEntity(Category).build()

        when:
        Diff diff = javers.compare("me", cat1, cat2)

        then:
        assertThat(diff).hasSize(40).hasAllOfType(ValueChange)
    }

    def "should manage empty diff on big graphs"() {
        given:
        Category cat1 = CategoryTestBuilder.category().deepWithChildNumber(5,5).build()
        Category cat2 = CategoryTestBuilder.category().deepWithChildNumber(5,5).build()
        Javers javers = javers().registerEntity(Category).build()

        when:
        Diff diff = javers.compare("me", cat1, cat2)

        then:
        diff.changes.size() == 0
    }

    @Ignore("need to calculate diff size")
    def "should manage full diff on big graphs"() {
        given:
        Category cat1 = CategoryTestBuilder.category().deepWithChildNumber(5,5).build()
        Category cat2 = CategoryTestBuilder.category(-1).deepWithChildNumber(5,5).build()
        Javers javers = javers().registerEntity(Category).build()

        when:
        Diff diff = javers.compare("me", cat1, cat2)

        then:
        assertThat(diff).hasSize(3906 * 2)
    }
}
