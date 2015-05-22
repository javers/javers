package org.javers.core;

public interface ProxyManager {
    <T> T unproxy(T entity);
}
