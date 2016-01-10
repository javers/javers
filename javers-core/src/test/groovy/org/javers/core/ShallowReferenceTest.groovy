package org.javers.core

import org.javers.core.model.Category
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserContact
import org.javers.core.model.DummyUserPhone
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers

/**
 * @author akrystian
 */
class ShallowReferenceTest extends Specification {

    def "should support ShallowReference"() {
        given:
        def left = new DummyUserContact("Tony")
        def right = new DummyUserContact("Tony")
        left.setPropertyWithShallowReferenceAnn(bossPhone)
        right.setPropertyWithShallowReferenceAnn(friendPhone)
        when:
        def javers = JaversBuilder.javers().build()
        def compare = javers.compare(left, right)
        then:
        !compare.hasChanges()

        where:
        bossPhone << [createDummyUserPhone(1, "BossPhone"), createDummyUserPhone(1, "BossPhone", new Category(1, "Work"))]
        friendPhone << [createDummyUserPhone(1, "FriendPhone"), createDummyUserPhone(1, "BossPhone", new Category(2, "Friends"))]
    }

    def "should not support ShallowReference for not annotated class"() {
        given:
        def left = new DummyUserContact("Tony")
        def right = new DummyUserContact("Tony")
        def boss = new DummyUser("John", "Smith")
        def copyBoss = new DummyUser("John", "Knight")
        left.person = boss
        right.person = copyBoss
        when:
        def javers = javers().build()
        def compare = javers.compare(left, right)
        then:
        compare.hasChanges()

    }

    private static DummyUserPhone createDummyUserPhone(long id, String number, Category category) {
        def boss
        boss = new DummyUserPhone();
        boss.id = id;
        boss.number = number
        boss.category = category
        boss
    }

    private static DummyUserPhone createDummyUserPhone(long id, String number) {
        def boss
        boss = new DummyUserPhone();
        boss.id = id;
        boss.number = number
        boss
    }
}