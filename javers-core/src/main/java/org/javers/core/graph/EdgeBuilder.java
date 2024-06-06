package org.javers.core.graph;

import org.javers.common.collections.EnumerableFunction;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.PropertyOwnerContext;
import org.javers.core.metamodel.type.*;

import java.util.Optional;

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
    AbstractSingleEdge buildSingleEdge(LiveNode node, JaversProperty singleRef) {
        Object rawReference = node.getPropertyValue(singleRef);
        OwnerContext ownerContext = createOwnerContext(node, singleRef);

        if (!singleRef.isShallowReference()){
            LiveCdo cdo = cdoFactory.create(rawReference, ownerContext);
            LiveNode targetNode = buildNodeStubOrReuse(cdo, node);
            return new SingleEdge(singleRef, targetNode);
        }
        return new ShallowSingleEdge(singleRef, cdoFactory.createId(rawReference, ownerContext));
    }

    private OwnerContext createOwnerContext(ObjectNode parentNode, JaversProperty property) {
        return new PropertyOwnerContext(parentNode.getGlobalId(), property.getName());
    }

    AbstractMultiEdge createMultiEdge(JaversProperty containerProperty, EnumerableType enumerableType, LiveNode node) {
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
                return buildNodeStubOrReuse(cdo, node);
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

    private LiveNode buildNodeStubOrReuse(LiveCdo cdo, LiveNode parent){
        if (nodeReuser.isGraphLevelReusable(cdo)){
            LiveNode reused = nodeReuser.getForReuse(cdo);
            // System.out.println("--- reused node (globally): " + reused.getGlobalId() +" <- "+ cdo.getGlobalId());
            return reused;
        }
        Optional<LiveNode> locallyReused = nodeReuser.locallyReusableValueObjectNode(cdo, parent);
        if (locallyReused.isPresent()) {
            LiveNode reused = locallyReused.get();
            // System.out.println("--- reused node (locally): " + reused.getGlobalId() +" <- "+ cdo.getGlobalId());
            return reused;
        }

        // System.out.println("-- regular node:" + cdo.getGlobalId());
        return buildNodeStub(cdo, Optional.of(parent));
    }

    LiveNode buildNodeStub(LiveCdo cdo, Optional<LiveNode> parent){
        LiveNode newStub = new LiveNode(cdo, parent);
        nodeReuser.enqueueStub(newStub);
        return newStub;
    }
}
