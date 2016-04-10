package org.javers.core.diff

import org.javers.core.JaversTestBuilder
import org.javers.core.graph.LiveGraph
import org.javers.core.graph.ObjectNode
import org.javers.core.metamodel.property.Property
import org.javers.core.metamodel.type.EntityType
import org.javers.core.metamodel.type.JaversType
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
abstract class AbstractDiffTest extends Specification {
    @Shared JaversTestBuilder javers = javersTestAssembly()

    ObjectNode buildGraph(def any) {
        javers.createLiveGraph(any).root()
    }

    LiveGraph buildLiveGraph(def any) {
        javers.createLiveGraph(any)
    }

    EntityType getEntity(Class forClass) {
        (EntityType)javers.typeMapper.getJaversType(forClass)
    }

    Property getManagedProperty(Class forClass, String propertyName) {
        javers.typeMapper.getJaversType(forClass).getProperty(propertyName)
    }

    Property getProperty(Class forClass, String propName) {
        getEntity(forClass).getProperty(propName)
    }

    RealNodePair realNodePair(def leftCdo, def rightCdo){
        new RealNodePair(buildGraph(leftCdo), buildGraph(rightCdo))
    }

    JaversType getJaversType(def javaType){
        javers.typeMapper.getJaversType(javaType)
    }
}
