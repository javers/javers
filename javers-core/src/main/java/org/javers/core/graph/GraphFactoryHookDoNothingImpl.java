package org.javers.core.graph;

/**
 * @author bartosz.walacik
 */
class GraphFactoryHookDoNothingImpl implements GraphFactoryHook {
    @Override
    public <T> T beforeAdd(T entity) {
        return entity;
    }
}
