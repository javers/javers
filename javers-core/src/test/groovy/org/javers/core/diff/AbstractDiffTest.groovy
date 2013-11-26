package org.javers.core.diff

import org.javers.core.JaversTestBuilder
import org.javers.core.model.DummyUser
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

    ObjectNode buildDummyUserNode(String userId) {
        buildDummyUserNode(userId,null)
    }

    Entity getEntity(Class forClass) {
        return (Entity)javersTestBuilder.entityManager.getByClass(forClass)
    }

    ObjectNode buildDummyUserNode(String userId, Sex sex) {
        buildGraph(DummyUserBuilder.dummyUser().withName(userId).withSex(sex).build())
    }
}
