package org.javers.core

import org.javers.core.model.Category
import org.javers.core.model.DummyUserContact
import org.javers.core.model.DummyUserPhone
import spock.lang.Specification

/**
 * @author akrystian
 */
class ShallowReferenceTest extends Specification {
    def javers

    def setup(){
        javers = JaversBuilder.javers().build()
    }

    def "should not compare properties annotated by ShallowReference"() {
        given:
        def left = new DummyUserContact("Tony")
        def right = new DummyUserContact("Tony")
        left.propertyWithShallowReferenceAnn = bossPhone
        right.propertyWithShallowReferenceAnn = friendPhone

        when:
        def compare = javers.compare(left, right)

        then:
        !compare.hasChanges()

        where:
        bossPhone << [new DummyUserPhone(1, "BossPhone"), new DummyUserPhone(1, "BossPhone", new Category(1, "Work"))]
        friendPhone << [new DummyUserPhone(1, "FriendPhone"), new DummyUserPhone(1, "BossPhone", new Category(2, "Friends"))]
    }
}