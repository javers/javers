package org.javers.core.graph

import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.object.ValueObjectId
import org.javers.core.model.*
import org.javers.repository.jql.ValueObjectIdDTO
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static NodeAssert.assertThat
import static org.javers.core.model.DummyUser.dummyUser
import static org.javers.core.model.DummyUserDetails.dummyUserDetails
import static org.javers.repository.jql.UnboundedValueObjectIdDTO.unboundedValueObjectId
import static org.javers.repository.jql.ValueObjectIdDTO.withUnboundedValueObjectOwner

/**
 * @author bartosz walacik
 */
abstract class ObjectGraphBuilderTest extends Specification {

    @Shared JaversTestBuilder javers

    ObjectGraphBuilder newBuilder(){
        new ObjectGraphBuilder(javers.typeMapper, javers.liveCdoFactory)
    }

    def "should provide dehydrated property value for references and values"(){
        given:
        def graphBuilder = newBuilder()
        def e = new SnapshotEntity(id:1, entityRef: new SnapshotEntity(id:2))

        when:
        def node = graphBuilder.buildGraph(e).root()

        then:
        with(node.getDehydratedPropertyValue("entityRef")) {
          it instanceof InstanceId
          it.value().endsWith("SnapshotEntity/2")
        }

        node.getDehydratedPropertyValue("id") == 1
    }

    def "should build one node graph from Entity"(){
        given:
        def graphBuilder = newBuilder()
        def user = dummyUser("Mad Kaz")

        when:
        def node = graphBuilder.buildGraph(user).root()

        then:
        NodeAssert.assertThat(node).hasNoEdges()
                .hasCdo(user)
                .hasInstanceId(DummyUser, "Mad Kaz")
    }

    def "should build graph starting from root ValueObject"(){
        given:
        def graphBuilder = newBuilder()
        def address = new DummyAddress("any","any")

        when:
        def node = graphBuilder.buildGraph(address).root()

        then:
        NodeAssert.assertThat(node).hasNoEdges()
                .hasCdo(address)
                .hasUnboundedValueObjectId(DummyAddress)
    }

    def "should build graph with ValueObject node"() {
        given:
        def graphBuilder = newBuilder()
        def user = dummyUserDetails(1).withAddress()

        when:
        def node = graphBuilder.buildGraph(user).root()

        then:
        NodeAssert.assertThat(node).hasCdo(user)
                .hasSingleEdge("dummyAddress")
                .andTargetNode()
                .hasNoEdges()
                .hasCdo(user.getDummyAddress())
                .hasGlobalIdValue(DummyUserDetails.name+"/1#dummyAddress")
    }

    def "should build two node graph for the same Entity"(){
        given:
        def graphBuilder = newBuilder()
        DummyUser user = dummyUser("Mad Kaz").withSupervisor("Mad Stach")

        when:
        def node = graphBuilder.buildGraph(user).root()

        then:
        NodeAssert.assertThat(node).hasEdges(1)
                .hasCdoId("Mad Kaz")
                .hasEdge("supervisor") //jump to EdgeAssert
                .isSingleEdgeTo("Mad Stach")
    }


    def "should build two node graph for different Entities"() {
        given:
        def graphBuilder = newBuilder()
        def user = dummyUser("Mad Kaz").withDetails()

        when:
        def node = graphBuilder.buildGraph(user).root()

        then:
        NodeAssert.assertThat(node).hasEdges(1)
                .hasCdoId("Mad Kaz")
                .hasEdge("dummyUserDetails")//jump to EdgeAssert
                .isSingleEdgeTo(1L)
    }

    def "should build graph from nested ValueObjects"(){
      given:
      def graphBuilder = newBuilder()
      def cat1 = new CategoryVo()
      def cat2 = new CategoryVo()
      def cat3 = new CategoryVo()
      def cat4 = new CategoryVo()
      cat1.parent = cat2
      cat2.parent = cat3
      cat3.parent = cat4

      when:
      def node = graphBuilder.buildGraph(cat1).root()

      then:
      NodeAssert.assertThat(node)
                .hasSingleEdge("parent")
                .andTargetNode()
                .hasValueObjectId("org.javers.core.model.CategoryVo/#parent")
                .and()
                .hasSingleEdge("parent")
                .andTargetNode()
                .hasValueObjectId("org.javers.core.model.CategoryVo/#parent/parent")
                .hasOwnerId("org.javers.core.model.CategoryVo/")
                . and()
                .hasSingleEdge("parent")
                .andTargetNode()
                .hasValueObjectId("org.javers.core.model.CategoryVo/#parent/parent/parent")
                .hasOwnerId("org.javers.core.model.CategoryVo/")

      and: "should get descendants"
      node.descendants(10).size() == 3
    }

    def "should build three nodes linear graph from Entities"() {
        given:
        //kaz0 - kaz1 - kaz2
        def graphBuilder = newBuilder()
        def kazik = dummyUser("Mad Kaz 0")
        def kazik1 = dummyUser("Mad Kaz 1")
        def kazik2 = dummyUser("Mad Kaz 2")
        kazik.supervisor = kazik1
        kazik1.supervisor = kazik2

        when:
        def node = graphBuilder.buildGraph(kazik).root()

        then:
        NodeAssert.assertThat(node).hasEdges(1)
                .hasCdoId("Mad Kaz 0")
                .hasSingleEdge("supervisor")
                .andTargetNode()
                .hasEdges(1)
                .hasCdoId("Mad Kaz 1")
                .hasSingleEdge("supervisor")
                .andTargetNode()
                .hasNoEdges()
                .hasCdoId("Mad Kaz 2")
    }

    def "should build four node graph with three levels"() {
        // kaz - kaz.details
        //    \
        //      stach - stach.details
        given:
        ObjectGraphBuilder graphBuilder = newBuilder()
        DummyUser stach = dummyUser("Mad Stach").withDetails(2)
        DummyUser kaz   = dummyUser("Mad Kaz").withDetails(1).withSupervisor(stach)

        when:
        def node = graphBuilder.buildGraph(kaz).root()

        then:
        NodeAssert.assertThat(node).hasEdges(2)
                .hasCdoId("Mad Kaz")
                .and().hasEdge("supervisor")
                .isSingleEdge()
                .andTargetNode()
                .hasCdoId("Mad Stach")
                .hasEdge("dummyUserDetails")
                .isSingleEdgeTo(2L)

        NodeAssert.assertThat(node).hasEdge("dummyUserDetails")
                .isSingleEdgeTo(1L)
    }


    def "should build graph with one SingleEdge and one MultiEdge"(){
        //       stach - details2
        //           \
        //           List
        //         /        |           \
        //      details1    details2    details3
        given:
        def graphBuilder = newBuilder()
        def stach = dummyUser("Mad Stach").withDetails(2).withDetailsList(3)

        when:
        def node = graphBuilder.buildGraph(stach).root()

        then:
        NodeAssert.assertThat(node).hasEdges(2)
        NodeAssert.assertThat(node).hasSingleEdge("dummyUserDetails")
        NodeAssert.assertThat(node).hasMultiEdge("dummyUserDetailsList").ofSize(3)

        and: "should get descendants"
        node.descendants(10).size() == 3
    }


    def "should build graph with three levels with MultiEdge"() {
        // kaz
        //    \
        //      stach
        //       \
        //        detailsList
        //         /   |   \
        //      id    id    id
        given:
        def graphBuilder = newBuilder()
        def stach = dummyUser("Stach").withDetailsList(3)
        def kaz   = dummyUser("Mad Kaz").withSupervisor(stach)

        when:
        def node = graphBuilder.buildGraph(kaz).root()

        then:
        NodeAssert.assertThat(node).hasCdoId("Mad Kaz")
                .hasEdge("supervisor")
                .isSingleEdgeTo("Stach")
                .andTargetNode()
                .hasMultiEdge("dummyUserDetailsList").ofSize(3)
    }


    def "should build graph with MultiEdge to node with MultiEdge"(){
        //              kaz
        //                 \
        //                 stach
        //              /    |    \
        //             rob   Em1  Em2
        //           /  |  \
        //          Em1 Em2 Em3
        given:
        def graphBuilder = newBuilder()
        def rob = dummyUser("rob").withEmployees(3)
        def stach = dummyUser("stach")
                .withEmployees([rob])
                .withEmployees(2)

        def kaz   = dummyUser("kaz").withSupervisor(stach)

        when:
        def node = graphBuilder.buildGraph(kaz).root()

        then:
        NodeAssert.assertThat(node).hasCdoId("kaz")
                .and().hasEdge("supervisor")
                .isSingleEdgeTo("stach")
                .andTargetNode()
                .hasEdge("employeesList")
                .isMultiEdge("Em1","Em2","rob")
                .andTargetNode("rob")
                .hasEdge("employeesList")
                .isMultiEdge("Em1", "Em2", "Em3")
    }

    def "should manage graph cycles"(){
        //superKaz
        //  \ \   \
        //   kaz   \
        //    \     \
        //      microKaz
        given:
        def graphBuilder = newBuilder()

        def superKaz = dummyUser("superKaz")
        def kaz   =    dummyUser("kaz").withSupervisor(superKaz)
        def microKaz = dummyUser("microKaz").withSupervisor(kaz)
        superKaz.employeesList = [ kaz, microKaz ]

        when:
        def node = graphBuilder.buildGraph(superKaz).root()

        then:
        //small cycle
        NodeAssert.assertThat(node).hasCdoId("superKaz")
                .hasEdge("employeesList")
                .isMultiEdge("kaz", "microKaz")
                .andTargetNode("kaz")
                .hasEdge("supervisor")
                .isSingleEdgeTo("superKaz")

        //large cycle
        NodeAssert.assertThat(node).hasCdoId("superKaz")
                .hasMultiEdge("employeesList")
                .andTargetNode("microKaz")
                .hasEdge("supervisor")
                .isSingleEdgeTo("kaz")
                .andTargetNode()
                .hasEdge("supervisor")
                .isSingleEdgeTo("superKaz")

        and: "should get descendants"
        node.descendants(10).size() == 2
    }

    def "should build graph with primitive types Set"() {
        given:
        ObjectGraphBuilder graphBuilder = newBuilder()
        DummyUser dummyUser = dummyUser("name").withStringsSet(["1", "2", "3"] as Set)

        when:
        def node = graphBuilder.buildGraph(dummyUser).root()

        then:
        NodeAssert.assertThat(node).hasNoEdges()
    }


    def "should build graph with primitive types List"(){
        given:
        ObjectGraphBuilder graphBuilder = newBuilder()
        DummyUser dummyUser = dummyUser("name").withIntegerList([1, 2, 3, 4])

        when:
        def node = graphBuilder.buildGraph(dummyUser).root()

        then:
        NodeAssert.assertThat(node).hasNoEdges()
    }

    @Unroll
    def "should build graph with #containerType<#managedType> MultiEdge"() {
        given:
        ObjectGraphBuilder graphBuilder = newBuilder()

        when:
        def node = graphBuilder.buildGraph(cdo).root()

        then:
        assertThat(node).hasMultiEdge(propertyName).ofSize(2)

        where:
        managedType  << ["ValueObject"] * 3 + ["Entity"] *3
        containerType << ["Set","Array", "List"] * 2
        propertyName <<  ["setOfValueObjects","arrayOfValueObjects","listOfValueObjects", "setOfEntities","arrayOfEntities", "listOfEntities"]
        cdo << [
                 new SnapshotEntity(setOfValueObjects:   [new DummyAddress("London"), new DummyAddress("City")]) ,
                 new SnapshotEntity(arrayOfValueObjects: [new DummyAddress("London"), new DummyAddress("City")]) ,
                 new SnapshotEntity(listOfValueObjects:  [new DummyAddress("London"), new DummyAddress("City")]) ,
                 new SnapshotEntity(setOfEntities:       [new SnapshotEntity(id:2),   new SnapshotEntity(id:3)]) ,
                 new SnapshotEntity(arrayOfEntities:     [new SnapshotEntity(id:2),   new SnapshotEntity(id:3)]) ,
                 new SnapshotEntity(listOfEntities:      [new SnapshotEntity(id:2),   new SnapshotEntity(id:3)])
               ]
    }

    @Unroll
    def "should build graph with Map<#keyType, #valueType>  MultiEdge"() {
        given:
        ObjectGraphBuilder graphBuilder = newBuilder()

        when:
        def node = graphBuilder.buildGraph(cdo).root()

        then:
        assertThat(node).hasMultiEdge(propertyName).ofSize(2)

        where:
        keyType <<   ["Entity", "Primitive"]
        valueType << ["Entity", "ValueObject"]
        propertyName <<  ["mapOfEntities","mapPrimitiveToVO"]
        cdo << [
                new SnapshotEntity(mapOfEntities:    [(new SnapshotEntity(id:2)): new SnapshotEntity(id:3)]),
                new SnapshotEntity(mapPrimitiveToVO: ["key": new DummyAddress("London"), "key2": new DummyAddress("City")])
               ]
    }

    def "should assign proper hashes to ValueObjects in Set multi edge"() {
        given:
        def graphBuilder = newBuilder()
        def user = new SnapshotEntity(id:1, setOfValueObjects:[
                 new DummyAddress("London"),
                 new DummyAddress("London City")
        ])

        when:
        LiveNode node = graphBuilder.buildGraph(user).root()

        then:
        def expectedIds = [
                javers.valueObjectId(1, SnapshotEntity, "setOfValueObjects/"+javers.addressHash("London")),
                javers.valueObjectId(1, SnapshotEntity, "setOfValueObjects/"+javers.addressHash("London City"))
        ] as Set

        node.getEdge("setOfValueObjects").references.collect{it.globalId} as Set == expectedIds
    }

    def "should assign proper indexes to ValueObjects in List multi edge"() {
        given:
        def graphBuilder = newBuilder()
        def dummyUserDetails = new DummyUserDetails(id:5, addressList:
                [new DummyAddress("warszawa", "mokotowska"),
                 new DummyAddress("warszawa", "wolska")])

        when:
        def node = graphBuilder.buildGraph(dummyUserDetails).root()

        then:
        assertThat(node).hasMultiEdge("addressList").refersToGlobalIds(
                [new ValueObjectIdDTO(DummyUserDetails, 5,"addressList/0"),
                 new ValueObjectIdDTO(DummyUserDetails, 5,"addressList/1")
                ])
    }

    def "should support cycles on unbounded ValueObjects"() {
        given:
        ObjectGraphBuilder graphBuilder = newBuilder()

        def child1 = new CategoryVo("child1")
        def child2 = new CategoryVo("child2")
        def root = new CategoryVo("root")
        root.addChild(child1).addChild(child2)

        when:
        def node = graphBuilder.buildGraph(root).root()

        then:
        assertThat(node).hasGlobalId(unboundedValueObjectId(CategoryVo))
        assertThat(node).hasMultiEdge("children").refersToGlobalIds([
                withUnboundedValueObjectOwner(CategoryVo, "children/0"),
                withUnboundedValueObjectOwner(CategoryVo, "children/1")
        ])
        assertThat(node).hasMultiEdge("children")
                        .andFirstTargetNode()
                        .hasSingleEdge("parent").andTargetNode().hasGlobalId(unboundedValueObjectId(CategoryVo))
    }

    def "should support large graphs (more than 10000 edges)"() {
        given:
        def root = new CategoryC(0)
        def parent = root
        10000.times {
            def child = new CategoryC(parent.id+1)
            parent.addChild(child)
            parent = child
        }

        def graphBuilder = newBuilder()

        when:
        def rootNode = graphBuilder.buildGraph(root).root()

        then:
        def node = rootNode
        (10000-1).times {
            node = node.getEdge("categories").references[0]
            assertThat(node).hasMultiEdge("categories")
            assertThat(node).hasSingleEdge("parent")
        }

        and: "should limit descendants traversal to given depth"
        rootNode.descendants(1).size() == 1
        rootNode.descendants(2).size() == 2
        rootNode.descendants(3).size() == 3
    }

    @Unroll
    def "should build graph with Optional<#managedType> SingleEdge"() {
        given:
        def graphBuilder = newBuilder()

        when:
        def node = graphBuilder.buildGraph(cdo).root()

        then:
        assertThat(node).hasMultiEdge(propertyName).ofSize(1)

        where:
        managedType  << ["ValueObject", "Entity"]
        propertyName <<  ["optionalValueObject", "optionalEntity"]
        cdo << [
                new SnapshotEntity(optionalValueObject:  Optional.of(new DummyAddress("London"))) ,
                new SnapshotEntity(optionalEntity: Optional.of(new SnapshotEntity(id:1)))
        ]
    }

    def "should ignore empty Optional"() {
        given:
        def graphBuilder = newBuilder()

        when:
        def cdo = new SnapshotEntity(optionalValueObject:  Optional.empty())
        def node = graphBuilder.buildGraph(cdo).root()

        then:
        assertThat(node).hasNoEdges()
    }
}
