package org.javers.core.graph;

import org.javers.core.MappingStyle;
import org.javers.core.graph.wrappers.SpecifiedClassCollectionWrapper;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.EnumerableType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;

/**
 * @author pawelszymczyk
 */
public class CollectionsGraphBuilder {
    private final TypeMapper typeMapper;
    private final CollectionsCdoFactory collectionsCdoFactory;
    private final ObjectGraphBuilder objectGraphBuilder;

    private final NodeReuser nodeReuser = new NodeReuser();
    private final EdgeBuilder edgeBuilder;

    public CollectionsGraphBuilder(TypeMapper typeMapper, LiveCdoFactory cdoFactory, CollectionsCdoFactory collectionsCdoFactory) {
        this.typeMapper = typeMapper;
        this.collectionsCdoFactory = collectionsCdoFactory;
        this.edgeBuilder = new EdgeBuilder(typeMapper, nodeReuser, cdoFactory);
        this.objectGraphBuilder = new ObjectGraphBuilder(typeMapper, cdoFactory, nodeReuser);
    }

    public LiveGraph buildGraph(SpecifiedClassCollectionWrapper wrappedCollection, final Class clazz, MappingStyle mappingStyle) {
        Cdo cdo = collectionsCdoFactory.createCdo(wrappedCollection, clazz, mappingStyle);
        ObjectNode root = edgeBuilder.buildNodeStub(cdo);
        buildCollectionEdge(nodeReuser.pollStub(), clazz);

        return objectGraphBuilder.buildLeafs(root);
    }

    private void buildCollectionEdge(ObjectNode node, Class clazz) {
        nodeReuser.saveForReuse(node);

        Property property = containerProperty(node);
        EnumerableType enumerableType = typeMapper.getPropertyType(property);

        if (typeMapper.getJaversType(clazz) instanceof ManagedType) {
            node.addEdge(edgeBuilder.createMultiEdge(property, enumerableType, node));
        }
    }

    private Property containerProperty(ObjectNode node) {
        return node.getManagedType().getProperties().iterator().next();
    }
}
