package org.javers.core.graph;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Predicate;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final Map<Cdo, ObjectNode> reverseCdoIdMap;

    public ObjectGraphBuilder(TypeMapper typeMapper) {
        this.reverseCdoIdMap = new HashMap<>();
        this.typeMapper = typeMapper;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * @param handle Client's domain object, instance of Entity or ValueObject.
     *               It should be root of an aggregate, tree root
     *               or any node in objects graph from where all other nodes are navigable
     * @return graph node
     */
    public ObjectNode buildGraph(Object handle) {
        Cdo cdo = asCdo(handle, null);
        logger.debug("building objectGraph for handle [{}] ...",cdo);

        ObjectNode root = buildNode(cdo);

        logger.debug("graph assembled, object nodes: {}",reverseCdoIdMap.size());
        switchToBuilt();
        return root;
    }

    /**
     * recursive
     */
    private ObjectNode buildNode(Cdo cdo) {
        argumentIsNotNull(cdo);
        //logger.debug(".. creating node for: {}",cdo);

        ObjectNode node = buildNodeStubAndSaveForReuse(cdo);
        buildEdges(node);
        return node;
    }

    private void switchToBuilt() {
        if (built){
            throw new IllegalStateException("ObjectGraphBuilder is stateful builder (not a Service)");
        }
        built = true;
    }

    private void buildEdges(ObjectNode node) {
        buildSingleEdges(node);
        buildMultiEdges(node);
    }

    private void buildSingleEdges(ObjectNode node) {
        for (Property singleRef : getSingleReferences(node.getManagedClass())) {
            if (singleRef.isNull(node.wrappedCdo())) {
                continue;
            }

            Object referencedRawCdo = singleRef.get(node.wrappedCdo());
            ObjectNode referencedNode = buildNodeOrReuse(asCdo(referencedRawCdo,
                                                         createOwnerContext(node, singleRef)));

            Edge edge = new SingleEdge(singleRef, referencedNode);
            node.addEdge(edge);
        }
    }

    private List<Property> getSingleReferences(ManagedClass managedClass) {
        return managedClass.getProperties(new Predicate<Property>() {
            public boolean apply(Property property) {
                return (typeMapper.isEntityReferenceOrValueObject(property));
            }
        });
    }

    private void buildMultiEdges(ObjectNode node) {
        for (Property containerProperty : getNonEmptyEnumerablesWithManagedClasses(node))  {
            EnumerableType enumerableType = typeMapper.getPropertyType(containerProperty);

            //looks like we have Container or Map with Entity references or Value Objects
            MultiEdge multiEdge = createMultiEdge(containerProperty, enumerableType, node);

            node.addEdge(multiEdge);
        }
    }

    private List<Property> getNonEmptyEnumerablesWithManagedClasses(final ObjectNode node) {
        return node.getManagedClass().getProperties(new Predicate<Property>() {
            public boolean apply(Property property) {
                JaversType javersType = typeMapper.getPropertyType(property);
                if (! (javersType instanceof EnumerableType)) {
                    return false;
                }
                EnumerableType enumerableType = (EnumerableType)javersType;

                Object container = property.get(node.wrappedCdo());
                if (enumerableType.isEmpty(container)) {
                    return false;
                }

                if (property.isNull(node.wrappedCdo())) {
                    return false;
                }
                return (typeMapper.isContainerOfManagedClasses(enumerableType) ||
                        typeMapper.isMapWithManagedClass(enumerableType)
                  );
            }
        });
    }

    private MultiEdge createMultiEdge(Property containerProperty, EnumerableType enumerableType, ObjectNode node) {
        MultiEdge multiEdge = new MultiEdge(containerProperty);
        OwnerContext owner = createOwnerContext(node, containerProperty);

        EnumerableFunction edgeBuilder = new MultiEdgeBuilderFunction(multiEdge, enumerableType, typeMapper);
        Object container = containerProperty.get(node.wrappedCdo());
        enumerableType.map(container, edgeBuilder, owner);

        return multiEdge;
    }

    /**
     * @author bartosz walacik
     */
    private class MultiEdgeBuilderFunction extends AbstractMapFunction {
        final MultiEdge multiEdge;

        MultiEdgeBuilderFunction(MultiEdge multiEdge, EnumerableType enumerableType, TypeMapper typeMapper) {
            super(enumerableType, typeMapper);
            this.multiEdge = multiEdge;
        }

        @Override
        public Object apply(Object input, OwnerContext enumerationAwareOwnerContext) {
            if (!isManagedPosition(enumerationAwareOwnerContext)){
                return input;
            }

            ObjectNode objectNode = buildNodeOrReuse(asCdo(input, enumerationAwareOwnerContext));
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

    private ObjectNode buildNodeStubAndSaveForReuse(Cdo cdo) {
        ObjectNode nodeStub = new ObjectNode(cdo);
        reverseCdoIdMap.put(cdo, nodeStub);
        return nodeStub;
    }

    private ObjectNode buildNodeOrReuse(Cdo referencedCdo){
        //reuse
        if (reverseCdoIdMap.containsKey(referencedCdo)) {
            return reverseCdoIdMap.get(referencedCdo);
        }

        //build
        return buildNode(referencedCdo);//recursion here
    }

    private Cdo asCdo(Object targetCdo, OwnerContext owner){
        GlobalCdoId globalId = GlobalIdFactory.createId(targetCdo, getManagedCLass(targetCdo), owner);
        return new CdoWrapper(targetCdo, globalId);
    }

    private ManagedClass getManagedCLass(Object cdo) {
        Validate.argumentIsNotNull(cdo);
        return  typeMapper.getManagedClass(cdo.getClass());
    }

    private OwnerContext createOwnerContext(ObjectNode node, Property property) {
        return new OwnerContext(node.getGlobalCdoId(), property.getName());
    }
}