package org.javers.core.metamodel.object;

import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.property.Property;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bartosz walacik
 */
public class CdoSnapshotBuilder {
    private final GlobalCdoId globalCdoId;
    private final Map<Property, Object> state = new HashMap<>();
    private final LocalDateTime dateTime;
    private final String author;
    private CommitId commitId;

    private CdoSnapshotBuilder(GlobalCdoId globalCdoId, String author, LocalDateTime dateTime) {
        this.globalCdoId = globalCdoId;
        this.author = author;
        this.dateTime = dateTime;
    }

    public static CdoSnapshotBuilder cdoSnapshot(GlobalCdoId globalCdoId, String author, LocalDateTime dateTime){
        Validate.argumentIsNotNull(globalCdoId);
        return new CdoSnapshotBuilder(globalCdoId, author, dateTime);
    }

    public CdoSnapshotBuilder withPropertyValue(Property property, Object value){
        Validate.argumentIsNotNull(property);
        if (value == null){
            return this;
        }

        if (state.containsKey(property)){
            throw new JaversException(JaversExceptionCode.SNAPSHOT_STATE_VIOLATION);
        }

        state.put(property, value);
        return this;
    }

    public CdoSnapshot build(){
        CommitMetadata commitMetadata = new CommitMetadata(author, dateTime);
        CdoSnapshot cdoSnapshot = new CdoSnapshot(globalCdoId, commitMetadata, state);

        if (commitId != null) {
            cdoSnapshot.bindTo(commitId);
        }

        return cdoSnapshot;
    }

    public CdoSnapshotBuilder withCommitId(CommitId commitId) {
        this.commitId = commitId;
        return this;
    }
}
