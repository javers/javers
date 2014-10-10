package org.javers.core.diff

import org.javers.core.JaversTestBuilder
import org.javers.core.diff.appenders.ArrayChangeAppender
import org.javers.core.diff.appenders.ListChangeAppender
import org.javers.core.diff.appenders.MapChangeAppender
import org.javers.core.diff.appenders.SetChangeAppender
import org.javers.core.graph.LiveGraph
import org.javers.core.graph.ObjectNode
import org.javers.core.metamodel.property.Entity
import org.javers.core.metamodel.property.Property
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz walacik
 */
abstract class AbstractDiffTest extends Specification {
    @Shared JaversTestBuilder javers = javersTestAssembly()

    ObjectNode buildGraph(def any) {
        javers.createObjectGraphBuilder().buildGraph(any).root()
    }

    LiveGraph buildLiveGraph(def any) {
        javers.createObjectGraphBuilder().buildGraph(any)
    }

    Entity getEntity(Class forClass) {
        return (Entity)javers.typeMapper.getJaversType(forClass).managedClass
    }

    Property getProperty(Class forClass, String propName) {
        getEntity(forClass).getProperty(propName)
    }

    RealNodePair realNodePair(def leftCdo, def rightCdo){
        new RealNodePair(buildGraph(leftCdo), buildGraph(rightCdo))
    }

    ListChangeAppender listChangeAppender() {
        new ListChangeAppender(mapChangeAppender(), javers.typeMapper)
    }

    MapChangeAppender mapChangeAppender() {
        new MapChangeAppender(javers.typeMapper, javers.globalIdFactory)
    }

    ArrayChangeAppender arrayChangeAppender() {
        new ArrayChangeAppender(mapChangeAppender(), javers.typeMapper)
    }

    SetChangeAppender setChangeAppender() {
        new SetChangeAppender(mapChangeAppender(), javers.typeMapper, javers.globalIdFactory)
    }
}
