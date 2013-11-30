package org.javers.core.diff

import org.javers.core.JaversTestBuilder
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.model.mapping.Entity
import org.javers.model.object.graph.ObjectNode
import org.javers.test.builder.DummyUserBuilder
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.model.DummyUser.*

/**
 * @author bartosz walacik
 */
abstract class AbstractDiffTest extends Specification {
    @Shared JaversTestBuilder javersTestBuilder = javersTestAssembly()

    ObjectNode buildGraph(Object root) {
        javersTestBuilder.createObjectGraphBuilder().buildGraph(root)
    }

    Entity getEntity(Class forClass) {
        return (Entity)javersTestBuilder.entityManager.getByClass(forClass)
    }

    ObjectNode buildDummyUserNode(String userId, Sex sex) {
        buildGraph(DummyUserBuilder.dummyUser().withName(userId).withSex(sex).build())
    }

    ObjectNode buildDummyUserNode(DummyUser user) {
        buildGraph(user)
    }

    ObjectNode buildDummyUserNode(String userId) {
        buildDummyUserNode(userId,null)
    }

    ObjectNode buildDummyUserNode(String userId, boolean flag) {
        buildGraph(DummyUserBuilder.dummyUser().withName(userId).withFlag(flag).build())
    }

    ObjectNode buildDummyUserDetailsNode(DummyUserDetails dummyUserDetails) {
        buildGraph(dummyUserDetails)
    }
}
