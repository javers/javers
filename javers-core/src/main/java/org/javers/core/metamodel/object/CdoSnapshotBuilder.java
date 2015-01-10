package org.javers.core.metamodel.object;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.property.Property;

import java.util.HashMap;
import java.util.Map;

import static org.javers.core.metamodel.object.SnapshotType.*;

/**
 * @author bartosz walacik
 */
public class CdoSnapshotBuilder {
    private final GlobalId globalId;
    private final Map<Property, Object> state = new HashMap<>();
    private CommitMetadata commitMetadata;
    private SnapshotType type = UPDATE;

    private CdoSnapshotBuilder(GlobalId globalId, CommitMetadata commitMetadata) {
        this.globalId = globalId;
        this.commitMetadata = commitMetadata;
    }

    public static CdoSnapshotBuilder cdoSnapshot(GlobalId globalId, CommitMetadata commitMetadata){
        Validate.argumentIsNotNull(globalId);
        return new CdoSnapshotBuilder(globalId, commitMetadata);
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
        return new CdoSnapshot(globalId, commitMetadata, state, type);
    }

    public CdoSnapshotBuilder withType(SnapshotType type){
        this.type = type;
        return this;
    }

    @Deprecated
    public CdoSnapshotBuilder withInitial(boolean initial){
        if (initial){
            this.type = INITIAL;
        }
        else{
            this.type = UPDATE;
        }
        return this;
    }

    public CdoSnapshotBuilder withCommitMetadata(CommitMetadata commitMetadata) {
        this.commitMetadata = commitMetadata;
        return this;
    }
}
