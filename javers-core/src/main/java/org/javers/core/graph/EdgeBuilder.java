package org.javers.core.graph;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.MapEnumeratorContext;
import org.javers.core.metamodel.type.TypeMapper;

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

    /**
     * @return node stub, could be redundant so check reuse context
     */
    SingleEdge buildSingleEdge(ObjectNode node, Property singleRef) {
        Object rawReference = node.getPropertyValue(singleRef);

        Cdo cdo = asCdo(rawReference, createOwnerContext(node, singleRef));
        ObjectNode referencedNode = buildNodeStubOrReuse(cdo);

        return new SingleEdge(singleRef, referencedNode);
    }

    Cdo asCdo(Object target, OwnerContext owner){
        //TODO this if is nasty, how to refactor it?
        if (target instanceof GlobalCdoId){
            return cdoFactory.create(target, (GlobalCdoId)target);
        }
        else{
            GlobalCdoId globalId = GlobalIdFactory.createId(target, getManagedCLass(target), owner);
            return cdoFactory.create(target, globalId);
        }

    }

    ManagedClass getManagedCLass(Object cdo) {
        Validate.argumentIsNotNull(cdo);
        return  typeMapper.getManagedClass(cdo.getClass());
    }

    private OwnerContext createOwnerContext(ObjectNode parentNode, Property property) {
        return new OwnerContext(parentNode.getGlobalCdoId(), property.getName());
    }

    MultiEdge createMultiEdge(Property containerProperty, EnumerableType enumerableType, ObjectNode node, ObjectGraphBuilder objectGraphBuilder) {
        MultiEdge multiEdge = new MultiEdge(containerProperty);
        OwnerContext owner = createOwnerContext(node, containerProperty);

        EnumerableFunction edgeBuilder = new MultiEdgeBuilderFunction(multiEdge, enumerableType);
        Object container = node.getPropertyValue(containerProperty);
        enumerableType.map(container, edgeBuilder, owner);

        return multiEdge;
    }

    /**
     * @author bartosz walacik
     */
    private class MultiEdgeBuilderFunction extends AbstractMapFunction {
        final MultiEdge multiEdge;

        MultiEdgeBuilderFunction(MultiEdge multiEdge, EnumerableType enumerableType) {
            super(enumerableType, typeMapper);
            this.multiEdge = multiEdge;
        }

        @Override
        public Object apply(Object input, OwnerContext enumerationAwareOwnerContext) {
            if (!isManagedPosition(enumerationAwareOwnerContext)){
                return input;
            }

            ObjectNode objectNode = buildNodeStubOrReuse(asCdo(input, enumerationAwareOwnerContext));
            multiEdge.addReferenceNode(objectNode);
            return null;
        }

        boolean isManagedPosition(OwnerContext enumerationAwareOwnerContext){
            if (!isMap()){
                return true;
            }

            MapEnumeratorContext mapContext =  enumerationAwareOwnerContext.getEnumeratorContext();
            if (mapContext.isKey()){
                return getKeyType() instanceof ManagedType;
            }
            else {
                return getValueType() instanceof ManagedType;
            }
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
        return new ObjectNode(cdo);
    }
}
