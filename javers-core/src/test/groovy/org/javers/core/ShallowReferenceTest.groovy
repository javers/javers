package org.javers.core

import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import spock.lang.Ignore
import spock.lang.Specification


/**
 * @author akrystian
 */
class ShallowReferenceTest extends Specification {

    @Ignore
    def "should support ShallowReference"() {
        given:
        def boss = createDummyUserDetails(1,"Boss")
        def copyBoss = createDummyUserDetails(1,"CopyBoss")

        def left = new DummyUser("Tony")
        left.setPropertyWithShallowReferenceAnn(boss)
        def right = new DummyUser("Tony")
        right.setPropertyWithShallowReferenceAnn(copyBoss)

        when:
        def javers = JaversBuilder.javers().build()

        then:
        !javers.compare(left, right).hasChanges()

    }

    private static DummyUserDetails createDummyUserDetails(long id, String someValue) {
        def boss
        boss = new DummyUserDetails();
        boss.id = id;
        boss.someValue = someValue
        boss
    }
}