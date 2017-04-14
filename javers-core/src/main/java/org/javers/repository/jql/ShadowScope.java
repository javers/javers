package org.javers.repository.jql;

/**
 * @author bartosz.walacik
 */
public enum ShadowScope {

    /**
     * Shadows are created only from snapshots selected directly in main query.
     * <br/>
     *
     * This query is fast, because no additional queries are executed,
     * but shadows are shallow. In most cases, references are not resolved.
     */
    SHALLOW,

    /**
     * JaVers tries to restore deep shadow graphs. References
     * are resolved when they are inside selected commits.
     * <br/>
     *
     * This query could be slow, because n + 1 queries are executed
     * (where n is the number of commits selected in main query).
     */
    COMMIT_DEPTH
}
