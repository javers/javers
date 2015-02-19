package org.javers.repository.sql.finders;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.SnapshotType;
import org.joda.time.LocalDateTime;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
* @author bartosz walacik
*/
class SnapshotWideDto {
    String commitAuthor;
    LocalDateTime commitDate;
    String commitId;
    int snapshotPk;
    SnapshotType snapshotType;
    String snapshotPropertyName;
    String snapshotPropertyValue;

    SnapshotWideDto(ResultSet resultSet) {
        try {
            commitAuthor = resultSet.getString(COMMIT_AUTHOR);
            commitDate = new LocalDateTime(resultSet.getTimestamp(COMMIT_COMMIT_DATE));
            commitId = resultSet.getString(COMMIT_COMMIT_ID);
            snapshotPk = resultSet.getInt(SNAPSHOT_PK);
            snapshotType = SnapshotType.valueOf(resultSet.getString(SNAPSHOT_TYPE));
            snapshotPropertyName = resultSet.getString(SNAP_PROPERTY_NAME);
            snapshotPropertyValue = resultSet.getString(SNAP_PROPERTY_VALUE);
        } catch (SQLException e){
            throw new JaversException(JaversExceptionCode.SQL_EXCEPTION, e.getMessage());
        }
    }

    CommitMetadata getCommitMetadata(){
        return new CommitMetadata(commitAuthor, commitDate, CommitId.valueOf(commitId));
    }

    boolean hasProperty(){
        return snapshotPropertyName != null && !snapshotPropertyName.isEmpty();
    }
}
