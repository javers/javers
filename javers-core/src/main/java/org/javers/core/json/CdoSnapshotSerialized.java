package org.javers.core.json;

import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

public class CdoSnapshotSerialized {
    //commitMetadata
    private Map<String, String> commitProperties;
    private String commitAuthor;
    private LocalDateTime commitDate;
    private Instant commitDateInstant;
    private BigDecimal commitId;
    private long commitPk;

    //snapshot
    private long version;
    private String snapshotState; //JSON
    private String changedProperties; //JSON
    private String snapshotType;

    //globalId
    private String globalIdFragment;
    private String globalIdLocalId;
    private String globalIdTypeName;
    private String ownerGlobalIdFragment;
    private String ownerGlobalIdLocalId;
    private String ownerGlobalIdTypeName;

    public CdoSnapshotSerialized withCommitProperties(Map<String, String> commitProperties) {
        this.commitProperties = commitProperties;
        return this;
    }

    public CdoSnapshotSerialized withCommitAuthor(String commitAuthor) {
        this.commitAuthor = commitAuthor;
        return this;
    }

    public CdoSnapshotSerialized withCommitDateInstant(String commitDateInstant) {
        this.commitDateInstant = commitDateInstant != null ? Instant.parse(commitDateInstant) : null;
        return this;
    }

    public CdoSnapshotSerialized withCommitDate(Date commitDate) {
        this.commitDate = UtilTypeCoreAdapters.fromUtilDate(commitDate);
        return this;
    }

    public CdoSnapshotSerialized withCommitDate(LocalDateTime commitDate) {
        this.commitDate = commitDate;
        return this;
    }

    public CdoSnapshotSerialized withCommitId(BigDecimal commitId) {
        this.commitId = commitId;
        return this;
    }

    public CdoSnapshotSerialized withCommitPk(long commitPk) {
        this.commitPk = commitPk;
        return this;
    }


    public CdoSnapshotSerialized withVersion(long version) {
        this.version = version;
        return this;
    }

    public CdoSnapshotSerialized withSnapshotState(String snapshotState) {
        this.snapshotState = snapshotState;
        return this;
    }

    public CdoSnapshotSerialized withChangedProperties(String changedProperties) {
        this.changedProperties = changedProperties;
        return this;
    }

    public CdoSnapshotSerialized withSnapshotType(String snapshotType) {
        this.snapshotType = snapshotType;
        return this;
    }

    public CdoSnapshotSerialized withGlobalIdFragment(String globalIdFragment) {
        this.globalIdFragment = globalIdFragment;
        return this;
    }

    public CdoSnapshotSerialized withGlobalIdLocalId(String globalIdLocalId) {
        this.globalIdLocalId = globalIdLocalId;
        return this;
    }

    public CdoSnapshotSerialized withGlobalIdTypeName(String globalIdTypeName) {
        this.globalIdTypeName = globalIdTypeName;
        return this;
    }

    public CdoSnapshotSerialized withOwnerGlobalIdFragment(String ownerGlobalIdFragment) {
        this.ownerGlobalIdFragment = ownerGlobalIdFragment;
        return this;
    }

    public CdoSnapshotSerialized withOwnerGlobalIdLocalId(String ownerGlobalIdLocalId) {
        this.ownerGlobalIdLocalId = ownerGlobalIdLocalId;
        return this;
    }

    public CdoSnapshotSerialized withOwnerGlobalIdTypeName(String ownerGlobalIdTypeName) {
        this.ownerGlobalIdTypeName = ownerGlobalIdTypeName;
        return this;
    }

    public Map<String, String> getCommitProperties() {
        return commitProperties;
    }

    public String getCommitAuthor() {
        return commitAuthor;
    }

    public LocalDateTime getCommitDate() {
        return commitDate;
    }

    public Instant getCommitDateInstant() {
        return commitDateInstant;
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

    public String getChangedProperties() {
        return changedProperties;
    }

    public String getSnapshotType() {
        return snapshotType;
    }

    public String getGlobalIdFragment() {
        return globalIdFragment;
    }

    public String getGlobalIdLocalId() {
        return globalIdLocalId;
    }

    public String getGlobalIdTypeName() {
        return globalIdTypeName;
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

    public long getCommitPk() {
        return commitPk;
    }
}

