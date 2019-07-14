package org.javers.core.graph;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
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
    private static final int MAX_VO_HASHING_DEPTH = 1;

    private final TypeMapper typeMapper;
    private boolean built;
    private final EdgeBuilder edgeBuilder;
    private final NodeReuser nodeReuser = new NodeReuser();
    private final LiveCdoFactory cdoFactory;

    ObjectGraphBuilder(TypeMapper typeMapper, LiveCdoFactory cdoFactory) {
        Validate.argumentsAreNotNull(typeMapper, cdoFactory);
        this.typeMapper = typeMapper;
        this.cdoFactory = cdoFactory;
        this.edgeBuilder = new EdgeBuilder(typeMapper, nodeReuser, cdoFactory);
    }

    /**
     * @param handle domain object, instance of Entity or ValueObject.
     *               It should be root of an aggregate, tree root
     *               or any node in object graph from where all other nodes are navigable
     * @return graph nodes set
     */
    LiveGraph buildGraph(Object handle) {
        argumentIsNotNull(handle);

        LiveCdo cdo = cdoFactory.create(handle, null);
        // logger.debug("building objectGraph for handle [{}] ...",cdo);

        return buildGraphFromCdo(cdo);
    }

    LiveGraph buildGraphFromCdo(LiveCdo cdo) {
        argumentIsNotNull(cdo);

        LiveNode root = edgeBuilder.buildNodeStub(cdo);

        //we can't use recursion here, it could cause StackOverflow for large graphs
        while(nodeReuser.hasMoreStubs()){
            LiveNode stub = nodeReuser.pollStub();
            buildEdges(stub); //edgeBuilder should append new stubs to queue
        }

        logger.debug("live graph assembled, object nodes: {}, entities: {}, valueObjects: {}",
                nodeReuser.nodesCount(), nodeReuser.entitiesCount(), nodeReuser.voCount());

        List<LiveNode> nodes = nodeReuser.nodes();

        enrichHashes(nodes);
        switchToBuilt();

        return new LiveGraph(root, new HashSet<>(nodes));
    }

    private void enrichHashes(List<LiveNode> nodes) {
        nodes.forEach(this::enrichHashIfNeeded);
        nodes.forEach(this::reloadHashFromParentIfNeeded);
    }

    private void enrichHashIfNeeded(final LiveNode node) {
        node.getCdo().enrichHashIfNeeded(cdoFactory, () -> node.descendants(MAX_VO_HASHING_DEPTH));
    }

    private void reloadHashFromParentIfNeeded(final LiveNode node) {
        node.getCdo().reloadHashFromParentIfNeeded(cdoFactory);
    }

    private void buildEdges(LiveNode nodeStub) {
        nodeReuser.saveForReuse(nodeStub);
        buildSingleEdges(nodeStub);
        buildMultiEdges(nodeStub);
    }

    private void buildSingleEdges(LiveNode node) {
        for (JaversProperty singleRef : getSingleReferencesWithManagedTypes(node.getManagedType())) {
            if (node.isNull(singleRef)) {
                continue;
            }

            AbstractSingleEdge edge = edgeBuilder.buildSingleEdge(node, singleRef);
            node.addEdge(edge);
        }
    }

    private void buildMultiEdges(LiveNode node) {

        for (JaversProperty containerProperty : getNonEmptyEnumerablesWithManagedTypes(node))  {
            EnumerableType enumerableType = containerProperty.getType();

            //looks like we have Container or Map with Entity references or Value Objects
            AbstractMultiEdge multiEdge = edgeBuilder.createMultiEdge(containerProperty, enumerableType, node);
            node.addEdge(multiEdge);
        }
    }

    private void switchToBuilt() {
        if (built){
            throw new IllegalStateException("ObjectGraphBuilder is a stateful builder (not a Service)");
        }
        built = true;
    }

    private List<JaversProperty> getSingleReferencesWithManagedTypes(ManagedType managedType) {
        return managedType.getProperties(property -> property.getType() instanceof ManagedType);
    }

    private List<JaversProperty> getNonEmptyEnumerablesWithManagedTypes(final LiveNode node) {
        return node.getManagedType().getProperties(property -> {
            if (!(property.getType() instanceof EnumerableType)) {
                return false;
            }
            EnumerableType enumerableType = property.getType();

            Object container = node.getPropertyValue(property);
            if (enumerableType.isEmpty(container)) {
                return false;
            }

            if (node.isNull(property)) {
                return false;
            }
            return (typeMapper.isContainerOfManagedTypes(enumerableType) ||
                    typeMapper.isKeyValueTypeWithManagedTypes(enumerableType)
            );
        });
    }
}