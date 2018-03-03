package org.javers.core.graph;

import org.javers.common.collections.EnumerableFunction;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.type.*;

/**
 * @author bartosz walacik
 */
class EdgeBuilder {
    private final TypeMapper typeMapper;
    private final NodeReuser nodeReuser;
    private final CdoFactory cdoFactory;

    EdgeBuilder(TypeMapper typeMapper, NodeReuser nodeReuser, CdoFactory cdoFactory) {
        this.typeMapper = typeMapper;
        this.nodeReuser = nodeReuser;
        this.cdoFactory = cdoFactory;
    }

    String graphType(){
        return cdoFactory.typeDesc();
    }

    /**
     * @return node stub, could be redundant so check reuse context
     */
    AbstractSingleEdge buildSingleEdge(ObjectNode node, JaversProperty singleRef) {
        Object rawReference = node.getPropertyValue(singleRef);
        Cdo cdo = cdoFactory.create(rawReference, createOwnerContext(node, singleRef));

        if (!singleRef.isShallowReference()){
            ObjectNode targetNode = buildNodeStubOrReuse(cdo);
            return new SingleEdge(singleRef, targetNode);
        }
        return new ShallowSingleEdge(singleRef, cdo);
    }

    private OwnerContext createOwnerContext(ObjectNode parentNode, JaversProperty property) {
        return new PropertyOwnerContext(parentNode.getGlobalId(), property.getName());
    }

    MultiEdge createMultiEdge(JaversProperty containerProperty, EnumerableType enumerableType, ObjectNode node) {
        MultiEdge multiEdge = new MultiEdge(containerProperty);
        OwnerContext owner = createOwnerContext(node, containerProperty);

        Object container = node.getPropertyValue(containerProperty);

        EnumerableFunction edgeBuilder = null;
        if (enumerableType instanceof KeyValueType){
            edgeBuilder = new MultiEdgeMapBuilderFunction(multiEdge);
        } else if (enumerableType instanceof ContainerType){
            edgeBuilder = new MultiEdgeContainerBuilderFunction(multiEdge);
        }
        enumerableType.map(container, edgeBuilder, owner);

        return multiEdge;
    }

    private class MultiEdgeContainerBuilderFunction implements EnumerableFunction {
        private final MultiEdge multiEdge;

        public MultiEdgeContainerBuilderFunction(MultiEdge multiEdge) {
            this.multiEdge = multiEdge;
        }

        @Override
        public Object apply(Object input, EnumerationAwareOwnerContext context) {
            if (!isManagedPosition(input)){
                return input;
            }
            ObjectNode objectNode = buildNodeStubOrReuse(cdoFactory.create(input, context));
            multiEdge.addReferenceNode(objectNode);
            return input;
        }

        boolean isManagedPosition(Object input){
            return true;
        }
    }

    private class MultiEdgeMapBuilderFunction extends MultiEdgeContainerBuilderFunction {
        public MultiEdgeMapBuilderFunction(MultiEdge multiEdge) {
            super(multiEdge);
        }

        boolean isManagedPosition(Object input){
            if (input == null) {
                return false;
            }
            return typeMapper.getJaversType(input.getClass()) instanceof ManagedType;
        }
    }

    private ObjectNode buildNodeStubOrReuse(Cdo cdo){
        if (nodeReuser.isReusable(cdo)){
            return nodeReuser.getForReuse(cdo);
        }
        else {
            return buildNodeStub(cdo);
        }
    }

    ObjectNode buildNodeStub(Cdo cdo){
        ObjectNode newStub = new ObjectNode(cdo);
        nodeReuser.enqueueStub(newStub);
        return newStub;
    }
}
