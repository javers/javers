package org.javers.model.object.graph;

import org.javers.common.collections.Predicate;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.*;
import org.javers.core.metamodel.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Creates graph based on ObjectWrappers.
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

    /**
     * @param cdo Client's domain object, instance of Entity or ValueObject.
     *            It should be root of an aggregate, tree root
     *            or any node in objects graph from where all other nodes are navigable
     * @return graph node
     */
    public ObjectNode buildGraph(Object cdo) {
        ObjectNode root = buildNode(asCdo(cdo, null));
        logger.debug("done building objectGraph for root ["+root+"], nodes: " + reverseCdoIdMap.size());
        switchToBuilt();
        return root;
    }

    /**
     * recursive
     */
    private ObjectNode buildNode(Cdo cdo) {
        argumentIsNotNull(cdo);
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
                                                         new OwnerContext(node, singleRef.getName())));

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

    private List<Property> getCollectionsOfManagedClasses(ManagedClass managedClass) {
        return managedClass.getProperties(new Predicate<Property>() {
            public boolean apply(Property property) {
                return (typeMapper.isCollectionOfManagedClasses(property));
            }
        });
    }

    private void buildMultiEdges(ObjectNode node) {
        for (Property colProperty : getCollectionsOfManagedClasses(node.getManagedClass()))  {
            if (colProperty.isNull(node.unwrapCdo())) {
                continue;
            }

            //looks like we have collection of Entity references or Value Objects
            Collection collectionOfReferences = (Collection)colProperty.get(node.unwrapCdo());
            if (collectionOfReferences.isEmpty()){
                continue;
            }
            MultiEdge multiEdge = createMultiEdge(colProperty, collectionOfReferences,
                                                  new OwnerContext(node, colProperty.getName()));
            node.addEdge(multiEdge);
        }
    }

    private MultiEdge createMultiEdge(Property multiRef, Collection collectionOfReferences, OwnerContext owner) {
        MultiEdge multiEdge = new MultiEdge(multiRef);
        owner.startListIndex();
        for(Object referencedRawCdo : collectionOfReferences) {
            ObjectNode objectNode = buildNodeOrReuse(asCdo(referencedRawCdo, owner));
            multiEdge.addReferenceNode(objectNode);
            owner.incListIndex();
        }
        return multiEdge;
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
        ManagedClass targetManagedClass =  getManagedCLass(targetCdo);

        //TODO refactor these IFs
        if (targetManagedClass instanceof Entity) {
            Entity entity = (Entity)targetManagedClass;
            return new CdoWrapper(targetCdo, new InstanceId(targetCdo, entity));
        }
        else if (targetManagedClass instanceof ValueObject && owner != null) {
            ValueObject valueObject = (ValueObject)targetManagedClass;
            return new CdoWrapper(targetCdo, new ValueObjectId(valueObject, owner.getGlobalCdoId(), owner.path));
        }
        else if (targetManagedClass instanceof ValueObject && owner == null) {
            ValueObject valueObject = (ValueObject)targetManagedClass;
            return new CdoWrapper(targetCdo, new UnboundedValueObjectId(valueObject));
        }
        else {
            throw new IllegalStateException("not implemented");
        }

    }

    private ManagedClass getManagedCLass(Object cdo) {
        Validate.argumentIsNotNull(cdo);
        return  typeMapper.getManagedClass(cdo.getClass());
    }

    private class OwnerContext {
        final ObjectWrapper owner;
        final String path;

        OwnerContext(ObjectWrapper owner, String path) {
            this.owner = owner;
            this.path = path;
        }

        GlobalCdoId getGlobalCdoId() {
            return owner.getGlobalCdoId();
        }
    }
}