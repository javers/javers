package org.javers.core.graph;

import org.javers.core.metamodel.type.TypeMapper;

/**
 * @author bartosz walacik
 */
public class LiveGraphFactory {
    private final TypeMapper typeMapper;
    private final LiveCdoFactory liveCdoFactory;

    public LiveGraphFactory(TypeMapper typeMapper, LiveCdoFactory liveCdoFactory) {
        this.typeMapper = typeMapper;
        this.liveCdoFactory = liveCdoFactory;
    }

    /**
     * delegates to {@link ObjectGraphBuilder#buildGraph(Object)}
     */
    public ObjectNode createLiveGraph(Object handle){
        return new ObjectGraphBuilder(typeMapper,liveCdoFactory).buildGraph(handle);
    }
}
