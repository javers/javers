package org.javers.model.object.graph;

import org.javers.common.validation.Validate;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.domain.Cdo;
import org.javers.model.mapping.Entity;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.ManagedClass;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.ValueObject;
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
     * @param cdo Client's domain object, instance of managed Entity.
     *            It should be root of an aggregate, tree root
     *            or any node in objects graph from where all other nodes are navigable
     * @return graph node
     */
    public ObjectNode buildGraph(Object cdo) {
        ObjectNode root = buildNode(asCdo(cdo));
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
        List<Property> singleReferences = node.getEntity().getSingleReferences();
        for (Property singleRef : singleReferences)  {
            if (singleRef.isNull(node.unwrapCdo())) {
                continue;
            }

            Object referencedRawCdo = singleRef.get(node.unwrapCdo());
            ObjectNode referencedNode = buildNodeOrReuse(asCdo(referencedRawCdo));

            Edge edge = new SingleEdge(singleRef, referencedNode);
            node.addEdge(edge);
        }
    }

    private void buildMultiEdges(ObjectWrapper node) {
        List<Property> multiReferences = node.getEntity().getCollectionTypeProperties();
        for (Property multiRef : multiReferences)  {
            if (multiRef.isNull(node.unwrapCdo())) {
                continue;
            }
            Collection collectionOfReferences = (Collection)multiRef.get(node.unwrapCdo());
            if (collectionOfReferences.isEmpty()){
                continue;
            }
            MultiEdge multiEdge = createMultiEdge(multiRef, collectionOfReferences);
            node.addEdge(multiEdge);
        }
    }

    private MultiEdge createMultiEdge(Property multiRef, Collection collectionOfReferences) {
        MultiEdge multiEdge = new MultiEdge(multiRef);
        for(Object referencedRawCdo : collectionOfReferences) {
            ObjectNode objectNode = buildNodeOrReuse(asCdo(referencedRawCdo));
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

    private Cdo asCdo(Object cdo){
        return new Cdo(cdo, getEntity(cdo));
    }

    private Entity getEntity(Object cdo) {
        Validate.argumentIsNotNull(cdo);
        ManagedClass managedClass = entityManager.getByClass(cdo.getClass());
        if (managedClass instanceof ValueObject) {
            throw new JaversException(JaversExceptionCode.UNEXPECTED_VALUE_OBJECT, cdo.getClass().getName());
        }
        return (Entity)managedClass;
    }
}