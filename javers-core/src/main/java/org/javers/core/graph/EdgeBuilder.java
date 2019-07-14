package org.javers.core.graph;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
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
        OwnerContext ownerContext = createOwnerContext(node, singleRef);

        if (!singleRef.isShallowReference()){
            LiveCdo cdo = cdoFactory.create(rawReference, ownerContext);
            LiveNode targetNode = buildNodeStubOrReuse(cdo);
            return new SingleEdge(singleRef, targetNode);
        }
        return new ShallowSingleEdge(singleRef, cdoFactory.createId(rawReference, ownerContext));
    }

    private OwnerContext createOwnerContext(ObjectNode parentNode, JaversProperty property) {
        return new PropertyOwnerContext(parentNode.getGlobalId(), property.getName());
    }

    AbstractMultiEdge createMultiEdge(JaversProperty containerProperty, EnumerableType enumerableType, ObjectNode node) {
        OwnerContext owner = createOwnerContext(node, containerProperty);

        Object container = node.getPropertyValue(containerProperty);

        boolean isShallow = containerProperty.isShallowReference() ||
                hasShallowReferenceItems(enumerableType);

        EnumerableFunction itemMapper = (input, context) -> {
            if (!isShallow) {
                LiveCdo cdo = cdoFactory.create(input, context);
                return buildNodeStubOrReuse(cdo);
            } else {
                return cdoFactory.createId(input, context);
            }
        };

        EnumerableFunction edgeBuilder = createEdgeBuilder(enumerableType, itemMapper);

        Object mappedEnumerable = enumerableType.map(container, edgeBuilder, owner);
        if (!isShallow) {
            return new MultiEdge(containerProperty, mappedEnumerable);
        } else {
            return new ShallowMultiEdge(containerProperty, mappedEnumerable);
        }
    }

    private boolean hasShallowReferenceItems(EnumerableType enumerableType){
        if (enumerableType instanceof ContainerType) {
            ContainerType containerType = (ContainerType)enumerableType;
            return typeMapper.isShallowReferenceType(containerType.getItemType());
        }
        if (enumerableType instanceof KeyValueType) {
            KeyValueType keyValueType = (KeyValueType)enumerableType;
            return typeMapper.isShallowReferenceType(keyValueType.getKeyType()) ||
                   typeMapper.isShallowReferenceType(keyValueType.getValueType());
        }
        return false;
    }

    private EnumerableFunction createEdgeBuilder(EnumerableType enumerableType, EnumerableFunction itemMapper) {
        if (enumerableType instanceof KeyValueType){
            KeyValueType mapType = (KeyValueType)enumerableType;

            final boolean managedKeys = typeMapper.getJaversType(mapType.getKeyType()) instanceof ManagedType;
            final boolean managedValues = typeMapper.getJaversType(mapType.getValueType()) instanceof ManagedType;

            return (keyOrValue, context) -> {
                MapEnumerationOwnerContext mapContext = (MapEnumerationOwnerContext)context;

                if (managedKeys && mapContext.isKey()) {
                    return itemMapper.apply(keyOrValue, context);
                }

                if (managedValues && !mapContext.isKey()) {
                    return itemMapper.apply(keyOrValue, context);
                }

                return keyOrValue;
            };

        } else if (enumerableType instanceof ContainerType) {
            return itemMapper;
        } else {
            throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
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
