package org.javers.model.object.graph;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.Entity;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.ManagedClass;
import org.javers.model.mapping.Property;
import org.javers.model.mapping.ValueObject;

import java.util.Collection;
import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Creates graph based on ObjectWrappers
 *
 * @author bartosz walacik
 */
public class ObjectGraphBuilder {
    protected final EntityManager entityManager;

    public ObjectGraphBuilder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * @param cdo client's domain object, it should be root of an aggregate, tree root
     *            or any node in objects graph from all other nodes are navigable
     * @return graph node
     */
    public ObjectNode build(Object cdo) {
        argumentIsNotNull(cdo);
        ManagedClass managedClass = entityManager.getByClass(cdo.getClass());

        argumentIsNotNull(managedClass);
        if (managedClass instanceof ValueObject) {
            throw new JaversException(JaversExceptionCode.UNEXPECTED_VALUE_OBJECT, cdo.getClass().getName());
        }

        ObjectWrapper node = new ObjectWrapper(cdo, (Entity) managedClass);

        initEdges(node);

        return node;
    }

    private void initEdges(ObjectWrapper node) {
        initSingleEdge(node);
        initMultiEdge(node);
    }

    private void initMultiEdge(ObjectWrapper node) {
        List<Property> multiReferences = node.getEntity().getMultiReferences();
        for (Property multiRef : multiReferences)  {
            if (multiRef.isNull(node.getCdo())) {
                continue;
            }
            Object collectionRefCod = multiRef.get(node.getCdo());
            MultiEdge multiEdge = createMultiEdge(multiRef, collectionRefCod);
            node.addEdge(multiEdge);
        }
    }

    private MultiEdge createMultiEdge(Property multiRef, Object referencedCdo) {
        MultiEdge multiEdge = new MultiEdge(multiRef);
        for(Object o : (Collection)referencedCdo) {
            ObjectNode objectNode = build(o); //recursion here
            multiEdge.addReferenceNode(objectNode);
        }
        return multiEdge;
    }

    private void initSingleEdge(ObjectWrapper node) {
        List<Property> singleReferences = node.getEntity().getSingleReferences();
        for (Property singleRef : singleReferences)  {
            if (singleRef.isNull(node.getCdo())) {
                continue;
            }

            Object referencedCdo = singleRef.get(node.getCdo());
            ObjectWrapper referencedNode = (ObjectWrapper)build(referencedCdo);//recursion here

            Edge edge = new SingleEdge(singleRef, referencedNode);
            node.addEdge(edge);
        }
    }
}