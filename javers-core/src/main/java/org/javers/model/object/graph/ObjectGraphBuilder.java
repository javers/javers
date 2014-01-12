package org.javers.model.object.graph;

import org.javers.common.validation.Validate;
import org.javers.model.domain.Cdo;
import org.javers.model.domain.InstanceId;
import org.javers.model.domain.ValueObjectId;
import org.javers.model.mapping.*;
import org.javers.model.mapping.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Creates graph based on ObjectWrappers.
 * This is stateful builder (not a Service)
 *
 * @author bartosz walacik
 */
public class ObjectGraphBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ObjectGraphBuilder.class);

    private final EntityManager entityManager;
    private boolean built;
    private Map<Cdo, ObjectWrapper> reverseCdoIdMap;

    public ObjectGraphBuilder(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.reverseCdoIdMap = new HashMap<>();
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
        ObjectWrapper node = buildNodeStubAndSaveForReuse(cdo);
        buildEdges(node);
        return node;
    }

    private void switchToBuilt() {
        if (built){
            throw new IllegalStateException("ObjectGraphBuilder is stateful builder (not a Service)");
        }
        built = true;
    }

    private void buildEdges(ObjectWrapper node) {
        buildSingleEdges(node);
        buildMultiEdges(node);
    }

    private void buildSingleEdges(ObjectWrapper node) {
        List<Property> singleReferences = node.getManagedClass().getSingleReferences();
        for (Property singleRef : singleReferences)  {
            if (singleRef.isNull(node.unwrapCdo())) {
                continue;
            }

            Object referencedRawCdo = singleRef.get(node.unwrapCdo());
            ObjectNode referencedNode = buildNodeOrReuse(asCdo(referencedRawCdo, node));

            Edge edge = new SingleEdge(singleRef, referencedNode);
            node.addEdge(edge);
        }
    }

    private boolean isCollectionOfEntityReferences(CollectionType collectionType) {
        Class elementType = collectionType.getElementType();

        if (elementType == null || !entityManager.isManaged(elementType)) {
            return false;
        }

        return entityManager.getByClass(elementType) instanceof Entity;
    }

    private void buildMultiEdges(ObjectWrapper node) {
        List<Property> collectionProperties = node.getManagedClass().getCollectionTypeProperties();
        for (Property colProperty : collectionProperties)  {
            CollectionType collectionType = (CollectionType)colProperty.getType();
            if (!isCollectionOfEntityReferences((CollectionType)colProperty.getType())) {
                continue;
            }

            if (colProperty.isNull(node.unwrapCdo())) {
                continue;
            }

            //looks like we have collection of Entity references
            Collection collectionOfReferences = (Collection)colProperty.get(node.unwrapCdo());
            if (collectionOfReferences.isEmpty()){
                continue;
            }
            MultiEdge multiEdge = createMultiEdge(colProperty, collectionOfReferences, node);
            node.addEdge(multiEdge);
        }
    }

    private MultiEdge createMultiEdge(Property multiRef, Collection collectionOfReferences, ObjectWrapper owner) {
        MultiEdge multiEdge = new MultiEdge(multiRef);
        for(Object referencedRawCdo : collectionOfReferences) {
            ObjectNode objectNode = buildNodeOrReuse(asCdo(referencedRawCdo, owner));
            multiEdge.addReferenceNode(objectNode);
        }
        return multiEdge;
    }

    private ObjectWrapper buildNodeStubAndSaveForReuse(Cdo cdo) {
        ObjectWrapper nodeStub = new ObjectWrapper(cdo);
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

    private Cdo asCdo(Object targetCdo, ObjectWrapper owner){
        ManagedClass targetManagedClass =  getManagedCLass(targetCdo);

        //TODO refactor these IFs
        if (targetManagedClass instanceof Entity) {
            Entity entity = (Entity)targetManagedClass;
            return new Cdo(targetCdo, new InstanceId(targetCdo, entity));
        }
        else if (targetManagedClass instanceof ValueObject && owner != null) {
            ValueObject valueObject = (ValueObject)targetManagedClass;
            //TODO unsafe casting GlobalCdoId to InstanceId !
            //TODO what about this zonk?
            return new Cdo(targetCdo, new ValueObjectId(valueObject, (InstanceId)owner.getGlobalCdoId(), "zonk"));
        }
        else if (targetManagedClass instanceof ValueObject && owner == null) {
            throw new IllegalStateException("unbounded ValueObject not implemented");
        }
        else {
            throw new IllegalStateException("not implemented");
        }

    }

    private ManagedClass getManagedCLass(Object cdo) {
        Validate.argumentIsNotNull(cdo);
        return entityManager.getByClass(cdo.getClass());
    }
}