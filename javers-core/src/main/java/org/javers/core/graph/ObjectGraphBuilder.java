package org.javers.core.graph;

import org.javers.common.collections.Predicate;
import org.javers.common.exception.JaversException;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Creates graph based on ObjectNodes.
 * This is a stateful Builder (not a Service)
 *
 * @author bartosz walacik
 */
class ObjectGraphBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ObjectGraphBuilder.class);

    private final TypeMapper typeMapper;
    private boolean built;
    private final EdgeBuilder edgeBuilder;
    private final NodeReuser nodeReuser = new NodeReuser();

    public ObjectGraphBuilder(TypeMapper typeMapper, CdoFactory cdoFactory) {
        Validate.argumentsAreNotNull(typeMapper, cdoFactory);
        this.typeMapper = typeMapper;
        this.edgeBuilder = new EdgeBuilder(typeMapper, nodeReuser, cdoFactory);
    }

    /**
     * @param handle domain object, instance of Entity or ValueObject.
     *               It should be root of an aggregate, tree root
     *               or any node in object graph from where all other nodes are navigable
     * @return graph nodes set
     */
    public LiveGraph buildGraph(Object handle) {
        argumentIsNotNull(handle);

        Cdo cdo = edgeBuilder.asCdo(handle, null);
        // logger.debug("building objectGraph for handle [{}] ...",cdo);

        return buildGraphFromCdo(cdo);
    }

    LiveGraph buildGraphFromCdo(Cdo cdo) {
        argumentIsNotNull(cdo);

        ObjectNode root = edgeBuilder.buildNodeStub(cdo);

        //we can't use recursion here, it could cause StackOverflow for large graphs
        while(nodeReuser.hasMoreStubs()){
            ObjectNode stub = nodeReuser.pollStub();
            buildEdges(stub); //edgeBuilder should append new stubs to queue
        }

        logger.debug("{} graph assembled, object nodes: {}, entities: {}, valueObjects: {}",
                edgeBuilder.graphType(),
                nodeReuser.nodesCount(), nodeReuser.entitiesCount(), nodeReuser.voCount());
        switchToBuilt();
        return new LiveGraph(root, nodeReuser.nodes());
    }

    private void buildEdges(ObjectNode nodeStub) {
        nodeReuser.saveForReuse(nodeStub);
        buildSingleEdges(nodeStub);
        buildMultiEdges(nodeStub);
    }

    private void buildSingleEdges(ObjectNode node) {
        for (Property singleRef : getSingleReferencesWithManagedTypes(node.getManagedType())) {
            if (node.isNull(singleRef)) {
                continue;
            }

            SingleEdge edge = edgeBuilder.buildSingleEdge(node, singleRef);

            node.addEdge(edge);
        }
    }

    private void buildMultiEdges(ObjectNode node) {
        for (Property containerProperty : getNonEmptyEnumerablesWithManagedTypes(node))  {
            EnumerableType enumerableType = typeMapper.getPropertyType(containerProperty);

            //looks like we have Container or Map with Entity references or Value Objects
            MultiEdge multiEdge = edgeBuilder.createMultiEdge(containerProperty, enumerableType, node);

            node.addEdge(multiEdge);
        }
    }

    private void switchToBuilt() {
        if (built){
            throw new IllegalStateException("ObjectGraphBuilder is a stateful builder (not a Service)");
        }
        built = true;
    }

    private List<Property> getSingleReferencesWithManagedTypes(ManagedType managedType) {
        return managedType.getProperties(new Predicate<Property>() {
            public boolean apply(Property property) {
                JaversType javersType = typeMapper.getPropertyType(property);

                return javersType instanceof ManagedType;
            }
        });
    }

    private List<Property> getNonEmptyEnumerablesWithManagedTypes(final ObjectNode node) {
        return node.getManagedType().getProperties(new Predicate<Property>() {
            public boolean apply(Property property) {
                JaversType javersType = typeMapper.getPropertyType(property);
                if (!(javersType instanceof EnumerableType)) {
                    return false;
                }
                EnumerableType enumerableType = (EnumerableType) javersType;

                Object container = node.getPropertyValue(property);
                if (enumerableType.isEmpty(container)) {
                    return false;
                }

                if (node.isNull(property)) {
                    return false;
                }
                return (isContainerOfManagedTypes(enumerableType) ||
                    isMapWithManagedTypes(enumerableType)
                );
            }
        });
    }

    /**
     * is Set, List or Array of ManagedClasses
     */
    private boolean isContainerOfManagedTypes(JaversType javersType){
        if (! (javersType instanceof ContainerType)) {
            return false;
        }

        return isItemManagedType((ContainerType) javersType);
    }

    private boolean isItemManagedType(ContainerType containerType){
        return typeMapper.getJaversType(containerType.getItemType()) instanceof ManagedType;
    }

    /**
     * is Map with ManagedClass on Key or Value position
     */
    private boolean isMapWithManagedTypes(EnumerableType enumerableType) {
        if (! (enumerableType instanceof MapType)) {
            return false;
        }

        MapType mapType = (MapType)enumerableType;

        JaversType keyType = typeMapper.getJaversType(mapType.getKeyType());
        JaversType valueType = typeMapper.getJaversType(mapType.getValueType());

        return keyType instanceof ManagedType || valueType instanceof ManagedType;
    }

}