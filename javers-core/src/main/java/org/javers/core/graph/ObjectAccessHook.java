package org.javers.core.graph;

/**
 * Object access hook
 * <br><br>
 *
 * Used for accessing object before commit.
 * i.e. to unproxy hibernate object before comparison
 *
 * Needs to be idempotent because JaVers could call it more than once during diff.
 *
 */
public interface ObjectAccessHook {
    <T> T access(T entity);
}
