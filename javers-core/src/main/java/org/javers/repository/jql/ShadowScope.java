package org.javers.repository.jql;

/**
 * @author bartosz.walacik
 */
public enum ShadowScope {

    /**
     * Shadows are created only from snapshots selected directly in the JQL query.
     * <br/>
     *
     * This query is fast (no additional queries are executed)
     * but shadows are shallow. In most cases, references are not resolved.
     */
    SHALLOW,

    /**
     * JaVers tries to restore deep shadow graphs. References
     * are resolved when they exists in selected commits.
     * <br/>
     *
     * This query is slower than SHALLOW query,
     * because JaVers executes additional query for all
     * snapshots in commits touched by the JQL query.
     */
    COMMIT_DEPTH
}
