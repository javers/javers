package org.javers.core;

public interface IgnoredClassesStrategy {

    boolean isIgnored(Class<?> domainClass);
}
