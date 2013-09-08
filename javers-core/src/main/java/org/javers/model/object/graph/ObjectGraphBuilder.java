package org.javers.model.object.graph;

import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.Property;

import java.util.ArrayList;
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
        ObjectWrapper node = new ObjectWrapper(cdo, entityManager.getByClass(cdo.getClass()));

        initEdges(node);

        return node;
    }

    private void initEdges(ObjectWrapper node) {
        //List<Edge> edges = new ArrayList<>();
        //init SingleEdges
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

        //TODO implement support for multi-edges
    }

}