package org.javers.core.graph;

import org.javers.common.validation.Validate;
import org.javers.core.diff.ObjectGraph;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author pawelszymczyk
 */
public class CollectionsGraphBuilder {

    private final TypeMapper typeMapper;
    private final EdgeBuilder edgeBuilder;

    private final NodeReuser nodeReuser = new NodeReuser();
    private final ObjectGraphBuilder objectGraphBuilder;

    public CollectionsGraphBuilder(TypeMapper typeMapper, CdoFactory cdoFactory) {
        Validate.argumentsAreNotNull(typeMapper, cdoFactory);
        this.typeMapper = typeMapper;
        this.edgeBuilder = new EdgeBuilder(typeMapper, nodeReuser, cdoFactory);
        this.objectGraphBuilder = new ObjectGraphBuilder(typeMapper, cdoFactory, nodeReuser);
    }

    public <T> ObjectGraph buildGraph(Object handle, Class<T> clazz) {
        argumentIsNotNull(handle);

        Cdo cdo = edgeBuilder.asCdo(handle, null);
        ObjectNode root = edgeBuilder.buildNodeStub(cdo);
        buildCollectionEdge(nodeReuser.pollStub(), clazz);

        return objectGraphBuilder.buildLeafs(root);
    }

    private void buildCollectionEdge(ObjectNode node, Class clazz) {
        Validate.conditionFulfilled(node.getManagedClass().getProperties().size() == 1,
                "Collection should be wrapped into one field class: LiveGraphFactory.ListWrapper, LiveGraphFactory.SetWrapper or " +
                        "LiveGraphFactory.ArrayWrapper");

        nodeReuser.saveForReuse(node);

        Property property = containerProperty(node);
        EnumerableType enumerableType = typeMapper.getPropertyType(property);

        if (typeMapper.getJaversType(clazz) instanceof ManagedType) {
            node.addEdge(edgeBuilder.createMultiEdge(property, enumerableType, node));
        }
    }

    private Property containerProperty(ObjectNode node) {
        return node.getManagedClass().getProperties().get(0);
    }
}
