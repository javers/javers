package org.javers.core.graph;

public interface GraphFactoryHook {
    <T> T beforeAdd(T entity);
}
