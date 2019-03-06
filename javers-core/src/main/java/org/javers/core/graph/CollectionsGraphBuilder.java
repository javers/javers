package org.javers.core.graph;

/**
 * @author pawelszymczyk
 */
class CollectionsGraphBuilder {
    private final CollectionsCdoFactory collectionsCdoFactory;
    private final ObjectGraphBuilder objectGraphBuilder;

    public CollectionsGraphBuilder(ObjectGraphBuilder objectGraphBuilder, CollectionsCdoFactory collectionsCdoFactory) {
        this.collectionsCdoFactory = collectionsCdoFactory;
        this.objectGraphBuilder = objectGraphBuilder;
    }

    public LiveGraph buildGraph(CollectionWrapper wrappedCollection, final Class clazz) {
        LiveCdo cdo = collectionsCdoFactory.createCdo(wrappedCollection, clazz);

        return objectGraphBuilder.buildGraphFromCdo(cdo);
    }
}
