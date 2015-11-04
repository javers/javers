package org.javers.core.graph;

import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.type.TypeMapper;

/**
 * @author pawelszymczyk
 */
class CollectionsGraphBuilder {
    private final CollectionsCdoFactory collectionsCdoFactory;
    private final ObjectGraphBuilder objectGraphBuilder;

    public CollectionsGraphBuilder(TypeMapper typeMapper, LiveCdoFactory cdoFactory, CollectionsCdoFactory collectionsCdoFactory) {
        this.collectionsCdoFactory = collectionsCdoFactory;
        this.objectGraphBuilder = new ObjectGraphBuilder(typeMapper, cdoFactory);
    }

    public LiveGraph buildGraph(CollectionWrapper wrappedCollection, final Class clazz) {
        Cdo cdo = collectionsCdoFactory.createCdo(wrappedCollection, clazz);

        return objectGraphBuilder.buildGraphFromCdo(cdo);
    }
}
