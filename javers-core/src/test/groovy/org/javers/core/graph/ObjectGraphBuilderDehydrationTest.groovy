package org.javers.core.graph

import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.model.SnapshotEntity
import spock.lang.Shared
import spock.lang.Specification

class ObjectGraphBuilderDehydrationTest extends Specification {
    @Shared JaversTestBuilder javers = JaversTestBuilder.javersTestAssembly()

    ObjectGraphBuilder newBuilder(){
        new ObjectGraphBuilder(javers.typeMapper, javers.liveCdoFactory)
    }

    def "should provide dehydrated property value for List of references"(){
        given:
        def graphBuilder = newBuilder()
        def e = new SnapshotEntity(id:1, listOfEntities: [new SnapshotEntity(id:2), new SnapshotEntity(id:3)])

        when:
        def node = graphBuilder.buildGraph(e).root()

        then:
        with(node.getDehydratedPropertyValue("listOfEntities")) {
            it instanceof List
            it[0] instanceof InstanceId
            it[0].value().endsWith("SnapshotEntity/2")
            it[1].value().endsWith("SnapshotEntity/3")
        }
    }

    def "should provide dehydrated property value for Set of references"(){
        given:
        def graphBuilder = newBuilder()
        def e = new SnapshotEntity(id:1, setOfEntities: [new SnapshotEntity(id:2), new SnapshotEntity(id:3)] as Set)

        when:
        def node = graphBuilder.buildGraph(e).root()

        then:
        with(node.getDehydratedPropertyValue("setOfEntities")) {
            it instanceof Set
            it.size() == 2
            it.every{it instanceof InstanceId}
        }
    }

    def "should provide dehydrated property value for Array of references"(){
        given:
        def graphBuilder = newBuilder()
        def e = new SnapshotEntity(id:1, arrayOfEntities: [new SnapshotEntity(id:2), new SnapshotEntity(id:3)] as Set)

        when:
        def node = graphBuilder.buildGraph(e).root()

        then:
        with(node.getDehydratedPropertyValue("arrayOfEntities")) {
            it.class.array
            it.length == 2
            it.every{it instanceof InstanceId}
        }
    }

    def "should provide dehydrated property value for Map with value -> reference "(){
        given:
        def graphBuilder = newBuilder()
        def e = new SnapshotEntity(id:1, mapPrimitiveToEntity: ["a" : new SnapshotEntity(id:2)])

        when:
        def node = graphBuilder.buildGraph(e).root()

        then:
        with(node.getDehydratedPropertyValue("mapPrimitiveToEntity")) {
            def id = it.get("a")
            id instanceof InstanceId
            id.value().endsWith("SnapshotEntity/2")
        }
    }

    def "should provide dehydrated property value for Map with reference -> reference"(){
        given:
        def graphBuilder = newBuilder()
        def e = new SnapshotEntity(id:1, mapOfEntities: [(new SnapshotEntity(id:2)) : new SnapshotEntity(id:3)])

        when:

        def node = graphBuilder.buildGraph(e).root()

        then:
        Map map = node.getDehydratedPropertyValue("mapOfEntities")
        map.keySet().first() instanceof InstanceId
        map.keySet().first().value().endsWith("SnapshotEntity/2")
        map.values().first() instanceof InstanceId
        map.values().first().value().endsWith("SnapshotEntity/3")
    }
}
