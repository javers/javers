package org.javers.repository.jql;

/**
 * @author bartosz.walacik
 */
public enum ShadowScope {

    /**
     * Shadows are created only from snapshots selected directly in main query.
     * <br/>
     *
     * This query is fast, no additional queries are executed,
     * but shadows are shallow. In most cases, references are not resolved.
     */
    SHALLOW,

    /**
     * JaVers tries to restore deep shadow graphs. References
     * are resolved when they are inside selected commits.
     * <br/>
     *
     * This query is slower than SHALLOW query,
     * because JaVers executes additional query for all
     * snapshots with commit ids selected in the main query.
     */
    COMMIT_DEPTH
}
