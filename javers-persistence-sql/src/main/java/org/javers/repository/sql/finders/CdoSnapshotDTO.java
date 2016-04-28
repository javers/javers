package org.javers.repository.sql.finders;

import org.javers.core.metamodel.object.GlobalId;

import java.math.BigDecimal;
import java.sql.Timestamp;

class CdoSnapshotDTO {
    private final long commitPK;
    private final String commitAuthor;
    private final Timestamp commitDate;
    private final BigDecimal commitId;
    private final long version;
    private final String snapshotState;
    private final String snapshotChanged;
    private final String snapshotType;
    private final String snapshotManagedType;
    private GlobalId globalId;
    private String globalIdFragment;
    private String globalIdLocalId;
    private String ownerGlobalIdFragment;
    private String ownerGlobalIdLocalId;
    private String ownerGlobalIdTypeName;

    public CdoSnapshotDTO(long commitPK, String commitAuthor, Timestamp commitDate, BigDecimal commitId, long version, String snapshotState, String snapshotChanged, String snapshotType, String snapshotManagedType, String globalIdFragment, String globalIdLocalId, String ownerGlobalIdFragment, String ownerGlobalIdLocalId, String ownerGlobalIdTypeName) {
        this.commitPK = commitPK;
        this.commitAuthor = commitAuthor;
        this.commitDate = commitDate;
        this.commitId = commitId;
        this.version = version;
        this.snapshotState = snapshotState;
        this.snapshotChanged = snapshotChanged;
        this.snapshotType = snapshotType;
        this.snapshotManagedType = snapshotManagedType;
        this.globalIdFragment = globalIdFragment;
        this.globalIdLocalId = globalIdLocalId;
        this.ownerGlobalIdFragment = ownerGlobalIdFragment;
        this.ownerGlobalIdLocalId = ownerGlobalIdLocalId;
        this.ownerGlobalIdTypeName = ownerGlobalIdTypeName;
    }

    public CdoSnapshotDTO(long commitPK, String commitAuthor, Timestamp commitDate, BigDecimal commitId, long version, String snapshotState, String snapshotChanged, String snapshotType, String snapshotManagedType, GlobalId globalId) {
        this.commitPK = commitPK;
        this.commitAuthor = commitAuthor;
        this.commitDate = commitDate;
        this.commitId = commitId;
        this.version = version;
        this.snapshotState = snapshotState;
        this.snapshotChanged = snapshotChanged;
        this.snapshotType = snapshotType;
        this.snapshotManagedType = snapshotManagedType;
        this.globalId = globalId;
    }

    public long getCommitPK() {
        return commitPK;
    }

    public String getCommitAuthor() {
        return commitAuthor;
    }

    public Timestamp getCommitDate() {
        return commitDate;
    }

    public BigDecimal getCommitId() {
        return commitId;
    }

    public long getVersion() {
        return version;
    }

    public String getSnapshotState() {
        return snapshotState;
    }

    public String getSnapshotChanged() {
        return snapshotChanged;
    }

    public String getSnapshotType() {
        return snapshotType;
    }

    public String getSnapshotManagedType() {
        return snapshotManagedType;
    }

    public GlobalId getGlobalId() {
        return globalId;
    }

    public String getGlobalIdFragment() {
        return globalIdFragment;
    }

    public String getGlobalIdLocalId() {
        return globalIdLocalId;
    }

    public String getOwnerGlobalIdFragment() {
        return ownerGlobalIdFragment;
    }

    public String getOwnerGlobalIdLocalId() {
        return ownerGlobalIdLocalId;
    }

    public String getOwnerGlobalIdTypeName() {
        return ownerGlobalIdTypeName;
    }
}

