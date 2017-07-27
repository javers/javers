package org.javers.repository.jql;

/**
 * @author bartosz.walacik
 */
public enum ShadowScope {
    /**
     * Object shadows are created only from snapshots selected directly in the main JQL query.
     * <br/><br/>
     *
     * This query is fast (no additional queries are executed)
     * but shadows are shallow. Most object references <b>are nulled</b>.
     * <br/><br/>
     *
     * You can initialize referenced objects using the wider scopes:
     * commit-deep or commit-deep+.
     */
    SHALLOW,

    /**
     * JaVers restores commit-deep shadow graph. Referenced
     * objects are resolved <b>if they exist</b> in selected commits.
     * <br/><br/>
     *
     * <b>Caution!</b> Commit-deep doesn't mean full.
     * References to objects that aren't available in selected commits <b>are nulled</b>.
     * This may be observed as unexpected <i>gaps</i> in a shadow graph.<br/>
     * You can fill these gaps using Commit-deep+ scope.
     *
     * <br/><br/>
     * Commit-deep query is slower than shallow query,
     * because JaVers executes the additional query to load all
     * snapshots in commits touched by the main JQL query.
     */
    COMMIT_DEPTH,

    /**
     * JaVers tries to restore an original object graph
     * with (possibly) all object references resolved.
     * <br/><br/>
     *
     * <b>Caution!</b> Commit-deep+ doesn't mean full, it just
     * fills recursively potential gaps in the restored object graph.
     * <br/>
     * We have to stop somewhere. The query parameter <code>maxGapsToFill</code>
     * limits the number of objects that would be loaded.<br/>
     * Think of commit-deep+ scope as commit-deep with max N gaps filled.
     * <br/><br/>
     *
     * Commit-deep+ query is slower than commit-deep query.
     * JaVers executes up to N additional queries to fill potential gaps in the object graph created by the commit-deep query.
     */
    COMMIT_DEPTH_PLUS;

    public boolean isCommitDepthOrWider() {
        return this == COMMIT_DEPTH || this == COMMIT_DEPTH_PLUS;
    }
}
