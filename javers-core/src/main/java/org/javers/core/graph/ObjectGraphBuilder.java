package org.javers.core.graph;

import org.javers.common.collections.Predicate;
import org.javers.common.exception.JaversException;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.clazz.ManagedClass;
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
public class ObjectGraphBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ObjectGraphBuilder.class);

    private final TypeMapper typeMapper;
    private boolean built;
    private final EdgeBuilder edgeBuilder;
    private final NodeReuser nodeReuser;

    public ObjectGraphBuilder(TypeMapper typeMapper, CdoFactory cdoFactory) {
        this(typeMapper, cdoFactory, new NodeReuser());
    }

    ObjectGraphBuilder(TypeMapper typeMapper, CdoFactory cdoFactory, NodeReuser nodeReuser) {
        Validate.argumentsAreNotNull(typeMapper, cdoFactory);
        this.typeMapper = typeMapper;
        this.edgeBuilder = new EdgeBuilder(typeMapper, nodeReuser, cdoFactory);
        this.nodeReuser = new NodeReuser();
    }

    /**
     * @param handle domain object, instance of Entity or ValueObject.
     *               It should be root of an aggregate, tree root
     *               or any node in object graph from where all other nodes are navigable
     * @return graph nodes set
     */
    public LiveGraph buildGraph(Object handle) {
        ObjectNode root = buildRoot(handle);
        return buildLeafs(root);
    }

    private ObjectNode buildRoot(Object handle) {
        argumentIsNotNull(handle);

        Cdo cdo = edgeBuilder.asCdo(handle, null);
        // logger.debug("building objectGraph for handle [{}] ...",cdo);

        return edgeBuilder.buildNodeStub(cdo);
    }

    LiveGraph buildLeafs(ObjectNode root) {
        //we can't use recursion here, it could cause StackOverflow for large graphs
        while (nodeReuser.hasMoreStubs()) {
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
        for (Property singleRef : getSingleReferencesWithManagedClasses(node.getManagedClass())) {
            if (node.isNull(singleRef)) {
                continue;
            }

            SingleEdge edge = edgeBuilder.buildSingleEdge(node, singleRef);

            node.addEdge(edge);
        }
    }

    private void buildMultiEdges(ObjectNode node) {
        for (Property containerProperty : getNonEmptyEnumerablesWithManagedClasses(node)) {
            EnumerableType enumerableType = typeMapper.getPropertyType(containerProperty);

            //looks like we have Container or Map with Entity references or Value Objects
            MultiEdge multiEdge = edgeBuilder.createMultiEdge(containerProperty, enumerableType, node);

            node.addEdge(multiEdge);
        }
    }

    private void switchToBuilt() {
        if (built) {
            throw new IllegalStateException("ObjectGraphBuilder is a stateful builder (not a Service)");
        }
        built = true;
    }

    private List<Property> getSingleReferencesWithManagedClasses(ManagedClass managedClass) {
        return managedClass.getProperties(new Predicate<Property>() {
            public boolean apply(Property property) {
                JaversType javersType = typeMapper.getPropertyType(property);

                return javersType instanceof ManagedType;
            }
        });
    }

    private List<Property> getNonEmptyEnumerablesWithManagedClasses(final ObjectNode node) {
        return node.getManagedClass().getProperties(new Predicate<Property>() {
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
                return (isContainerOfManagedClasses(enumerableType) ||
                        isMapWithManagedClass(enumerableType)
                );
            }
        });
    }

    /**
     * is Set, List or Array of ManagedClasses
     *
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED if property type is not fully parametrized
     */
    private boolean isContainerOfManagedClasses(JaversType javersType) {
        if (!(javersType instanceof ContainerType)) {
            return false;
        }

        return isItemManagedType((ContainerType) javersType);
    }

    private boolean isItemManagedType(ContainerType containerType) {
        return typeMapper.getJaversType(containerType.getItemType()) instanceof ManagedType;
    }

    /**
     * is Map with ManagedClass on Key or Value position
     *
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED if property type is not fully parametrized
     */
    private boolean isMapWithManagedClass(EnumerableType enumerableType) {
        if (!(enumerableType instanceof MapType)) {
            return false;
        }

        MapType mapType = (MapType) enumerableType;

        JaversType keyType = typeMapper.getJaversType(mapType.getKeyType());
        JaversType valueType = typeMapper.getJaversType(mapType.getValueType());

        return keyType instanceof ManagedType || valueType instanceof ManagedType;
    }

}