package org.javers.core.graph;

/**
 * @author bartosz.walacik
 */
class ObjectAccessHookDoNothingImpl implements ObjectAccessHook {
    @Override
    public <T> T access(T entity) {
        return entity;
    }
}
