package org.javers.core.diff

import org.javers.core.JaversTestBuilder
import org.javers.core.diff.appenders.ArrayChangeAppender
import org.javers.core.diff.appenders.ListChangeAppender
import org.javers.core.diff.appenders.MapChangeAppender
import org.javers.core.diff.appenders.SetChangeAppender
import org.javers.core.graph.LiveGraph
import org.javers.core.graph.ObjectNode
import org.javers.core.metamodel.clazz.Entity
import org.javers.core.metamodel.clazz.ManagedClass
import org.javers.core.metamodel.property.Property
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
abstract class AbstractDiffTest extends Specification {
    @Shared protected JaversTestBuilder javers = javersTestAssembly()

    ObjectNode buildGraph(def any) {
        javers.createObjectGraphBuilder().buildGraph(any).root()
    }

    LiveGraph buildLiveGraph(def any) {
        javers.createObjectGraphBuilder().buildGraph(any)
    }

    Entity getEntity(Class forClass) {
        (Entity)javers.typeMapper.getJaversType(forClass).managedClass
    }

    Property getManagedProperty(Class forClass, String propertyName) {
        ManagedClass clazz = javers.typeMapper.getJaversType(forClass).managedClass;
        clazz.getProperty(propertyName)
    }

    Property getProperty(Class forClass, String propName) {
        getEntity(forClass).getProperty(propName)
    }

    RealNodePair realNodePair(def leftCdo, def rightCdo){
        new RealNodePair(buildGraph(leftCdo), buildGraph(rightCdo))
    }
}
