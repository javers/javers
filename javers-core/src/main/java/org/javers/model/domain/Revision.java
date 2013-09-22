package org.javers.model.domain;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Revision is a set of changes made by client on his domain objects so
 * diff between two CDO graphs.
 * It is similar to <i>commit</i> notion in GIT.
 * <br/>
 *
 * Revision is an entity in Javers domain
 *
 * @author bartosz walacik
 */
public class Revision {
    private final long id;
    private final List<Change> changes;
    private final String userId;
    private final LocalDateTime revisionDate;

    public Revision(long id, String userId) {
        argumentIsNotNull(id);
        argumentIsNotNull(userId);

        this.userId = userId;
        this.id = id;
        this.revisionDate = new LocalDateTime();
        this.changes = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    /**
     * user identifier in clients data base,
     * typically login or numeric id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * date when revision was made by user
     */
    public LocalDateTime getRevisionDate() {
        return revisionDate;
    }

    /**
     * @return unmodifiable list
     */
    public List<Change> getChanges() {
        return Collections.unmodifiableList(changes);
    }

    public void addChange(Change change) {
        changes.add(change);
    }
}
