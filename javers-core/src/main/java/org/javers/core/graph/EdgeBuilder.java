package org.javers.core.graph;

import org.javers.common.collections.EnumerableFunction;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.type.*;

/**
 * @author bartosz walacik
 */
class EdgeBuilder {
    private final NodeReuser nodeReuser;
    private final LiveCdoFactory cdoFactory;

    EdgeBuilder(NodeReuser nodeReuser, LiveCdoFactory cdoFactory) {
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

            if (context instanceof MapEnumerationOwnerContext) {
                // corner case, for Maps with primitive-or-value keys
                MapEnumerationOwnerContext mapContext = (MapEnumerationOwnerContext)context;
                if (!(mapContext.getCurrentType() instanceof ManagedType)) {
                    return input;
                }
            }

            if (!isShallow) {
                LiveCdo cdo = cdoFactory.create(input, context);
                return buildNodeStubOrReuse(cdo);
            } else {
                return cdoFactory.createId(input, context);
            }
        };

        Object mappedEnumerable = enumerableType.map(container, itemMapper, owner);

        if (!isShallow) {
            return new MultiEdge(containerProperty, mappedEnumerable);
        } else {
            return new ShallowMultiEdge(containerProperty, mappedEnumerable);
        }
    }

    private boolean hasShallowReferenceItems(EnumerableType enumerableType){
        if (enumerableType instanceof ContainerType) {
            ContainerType containerType = (ContainerType)enumerableType;
            return containerType.getItemJaversType() instanceof ShallowReferenceType;
        }
        if (enumerableType instanceof KeyValueType) {
            KeyValueType keyValueType = (KeyValueType)enumerableType;
            return keyValueType.getKeyJaversType() instanceof ShallowReferenceType ||
                   keyValueType.getValueJaversType() instanceof ShallowReferenceType;
        }
        return false;
    }

    private LiveNode buildNodeStubOrReuse(LiveCdo cdo){
        if (nodeReuser.isReusable(cdo)){
            return nodeReuser.getForReuse(cdo);
        }
        else {
            if (nodeReuser.isTraversed(cdo)) {
                return buildNodeDouble(nodeReuser.getForDouble(cdo), cdo);
            }
            return buildNodeStub(cdo);
        }
    }

    /**
     * A node's double is created when we stumble upon an already traversed instance of VO
     */
    private LiveNode buildNodeDouble(LiveNode originalNode, LiveCdo cdoDouble) {
        LiveNode newDouble = LiveNode.liveNodeDouble(originalNode, cdoDouble);
        nodeReuser.saveNodeDouble(newDouble);
        return newDouble;
    }

    LiveNode buildNodeStub(LiveCdo cdo){
        LiveNode newStub = new LiveNode(cdo);
        nodeReuser.enqueueStub(newStub);
        return newStub;
    }
}
