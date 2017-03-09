package org.javers.core.graph;

import org.javers.core.metamodel.object.Cdo;

/**
 * @author pawelszymczyk
 */
class CollectionsGraphBuilder {
    private final CollectionsCdoFactory collectionsCdoFactory;
    private final ObjectGraphBuilder objectGraphBuilder;

    CollectionsGraphBuilder(ObjectGraphBuilder objectGraphBuilder, CollectionsCdoFactory collectionsCdoFactory) {
        this.collectionsCdoFactory = collectionsCdoFactory;
        this.objectGraphBuilder = objectGraphBuilder;
    }

    LiveGraph buildGraph(CollectionWrapper wrappedCollection, final Class clazz) {
        Cdo cdo = collectionsCdoFactory.createCdo(wrappedCollection, clazz);

        return objectGraphBuilder.buildGraphFromCdo(cdo);
    }
}
