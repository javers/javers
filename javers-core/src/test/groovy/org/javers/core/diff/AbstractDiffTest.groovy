package org.javers.core.diff

import org.javers.core.JaversTestBuilder
import org.javers.core.graph.LiveGraph
import org.javers.core.metamodel.property.Entity
import org.javers.core.metamodel.property.Property
import org.javers.core.graph.ObjectNode
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
abstract class AbstractDiffTest extends Specification {
    @Shared JaversTestBuilder javers = javersTestAssembly()

    ObjectNode buildGraph(def any) {
        javers.createObjectGraphBuilder().buildGraph(any)
    }

    LiveGraph buildLiveGraph(def any) {
        new LiveGraph(javers.createObjectGraphBuilder().buildGraph(any))
    }

    Entity getEntity(Class forClass) {
        return (Entity)javers.typeMapper.getJaversType(forClass).managedClass
    }

    Property getProperty(Class forClass, String propName) {
        getEntity(forClass).getProperty(propName)
    }
}
