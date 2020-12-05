package org.javers.core;

/**
 * A strategy used in
 * {@link JaversBuilder#registerIgnoredClassesStrategy(IgnoredClassesStrategy)}
 */
@FunctionalInterface
public interface IgnoredClassesStrategy {

    /**
     * Allows to mark classes as ignored by Javers.
     * <br/><br/>
     *
     * When a class is ignored, all properties
     * (found in other classes) with this class type are ignored.
     * <br/><br/>
     *
     * Called in runtime once for each class
     *
     * @return true if a class should be ignored
     */
    boolean isIgnored(Class<?> domainClass);
}
