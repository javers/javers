package org.javers.core.graph;

import org.javers.common.collections.EnumerableFunction;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.type.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bartosz walacik
 */
class EdgeBuilder {
    private final TypeMapper typeMapper;
    private final NodeReuser nodeReuser;
    private final LiveCdoFactory cdoFactory;

    EdgeBuilder(TypeMapper typeMapper, NodeReuser nodeReuser, LiveCdoFactory cdoFactory) {
        this.typeMapper = typeMapper;
        this.nodeReuser = nodeReuser;
        this.cdoFactory = cdoFactory;
    }

    /**
     * @return node stub, could be redundant so check reuse context
     */
    AbstractSingleEdge buildSingleEdge(ObjectNode node, JaversProperty singleRef) {
        Object rawReference = node.getPropertyValue(singleRef);
        LiveCdo cdo = cdoFactory.create(rawReference, createOwnerContext(node, singleRef));

        if (!singleRef.isShallowReference()){
            LiveNode targetNode = buildNodeStubOrReuse(cdo);
            return new SingleEdge(singleRef, targetNode);
        }
        return new ShallowSingleEdge(singleRef, cdo);
    }

    private OwnerContext createOwnerContext(ObjectNode parentNode, JaversProperty property) {
        return new PropertyOwnerContext(parentNode.getGlobalId(), property.getName());
    }

    MultiEdge createMultiEdge(JaversProperty containerProperty, EnumerableType enumerableType, ObjectNode node) {
        OwnerContext owner = createOwnerContext(node, containerProperty);

        Object container = node.getPropertyValue(containerProperty);

        MultiEdgeContainerBuilderFunction edgeBuilder = null;
        if (enumerableType instanceof KeyValueType){
            edgeBuilder = new MultiEdgeMapBuilderFunction();
        } else if (enumerableType instanceof ContainerType){
            edgeBuilder = new MultiEdgeContainerBuilderFunction();
        }
        enumerableType.map(container, edgeBuilder, owner);

        return new MultiEdge(containerProperty, edgeBuilder.getNodesAccumulator());
    }

    private class MultiEdgeContainerBuilderFunction implements EnumerableFunction {
        private final List<LiveNode> nodesAccumulator = new ArrayList();

        @Override
        public Object apply(Object input, EnumerationAwareOwnerContext context) {
            if (!isManagedPosition(input)){
                return input;
            }
            LiveNode objectNode = buildNodeStubOrReuse(cdoFactory.create(input, context));
            nodesAccumulator.add(objectNode);
            return null;
        }

        boolean isManagedPosition(Object input){
            return true;
        }

        List<LiveNode> getNodesAccumulator() {
            return nodesAccumulator;
        }
    }

    private class MultiEdgeMapBuilderFunction extends MultiEdgeContainerBuilderFunction {
        boolean isManagedPosition(Object input){
            if (input == null) {
                return false;
            }
            return typeMapper.getJaversType(input.getClass()) instanceof ManagedType;
        }
    }

    private LiveNode buildNodeStubOrReuse(LiveCdo cdo){
        if (nodeReuser.isReusable(cdo)){
            return nodeReuser.getForReuse(cdo);
        }
        else {
            return buildNodeStub(cdo);
        }
    }

    LiveNode buildNodeStub(LiveCdo cdo){
        LiveNode newStub = new LiveNode(cdo);
        nodeReuser.enqueueStub(newStub);
        return newStub;
    }
}
