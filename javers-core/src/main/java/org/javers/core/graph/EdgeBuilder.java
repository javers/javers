package org.javers.core.graph;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.type.*;

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

        EnumerableFunction edgeBuilder = (input, context) -> {
            final LiveCdo cdo = cdoFactory.create(input, context);
            if (!containerProperty.isShallowReference()) {
                return buildNodeStubOrReuse(cdo);
            } else {
                return cdo.getGlobalId();
            }
        };

        if (enumerableType instanceof KeyValueType){
            KeyValueType mapType = (KeyValueType)enumerableType;
            edgeBuilder = new MultiEdgeMapBuilder(
                    typeMapper.getJaversType(mapType.getKeyType()) instanceof ManagedType,
                    typeMapper.getJaversType(mapType.getValueType()) instanceof ManagedType,
                    edgeBuilder
            );

        } else if (!(enumerableType instanceof ContainerType)) {
            throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
        }

        Object nodesEnumerable = enumerableType.map(container, edgeBuilder, owner);
        return new MultiEdge(containerProperty, nodesEnumerable);
    }

    private class MultiEdgeMapBuilder implements EnumerableFunction {
        private final boolean managedKeys;
        private final boolean managedValues;
        private final EnumerableFunction edgeBuilder;

        public MultiEdgeMapBuilder(boolean managedKeys, boolean managedValues, EnumerableFunction edgeBuilder) {
            this.managedKeys = managedKeys;
            this.managedValues = managedValues;
            this.edgeBuilder = edgeBuilder;
        }

        @Override
        public Object apply(Object keyOrValue, EnumerationAwareOwnerContext context) {
            MapEnumerationOwnerContext mapContext = (MapEnumerationOwnerContext)context;

            if (managedKeys && mapContext.isKey()) {
                return edgeBuilder.apply(keyOrValue, context);
            }

            if (managedValues && !mapContext.isKey()) {
                return edgeBuilder.apply(keyOrValue, context);
            }

            return keyOrValue;
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
