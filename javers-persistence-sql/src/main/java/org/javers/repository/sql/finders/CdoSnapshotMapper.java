package org.javers.repository.sql.finders;

import org.javers.common.collections.Pair;
import org.javers.core.json.CdoSnapshotSerialized;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

class CdoSnapshotMapper implements ObjectMapper<Pair<CdoSnapshotSerialized,Long>> {

    @Override
    public Pair<CdoSnapshotSerialized,Long> createObject(ResultSet resultSet) throws SQLException {
        return new Pair<>(new CdoSnapshotSerialized()
                .withCommitAuthor(resultSet.getString(COMMIT_AUTHOR))
                .withCommitDate(LocalDateTime.fromDateFields(resultSet.getTimestamp(COMMIT_COMMIT_DATE)))
                .withCommitId(resultSet.getBigDecimal(COMMIT_COMMIT_ID))
                .withVersion(resultSet.getLong(SNAPSHOT_VERSION))
                .withSnapshotState(resultSet.getString(SNAPSHOT_STATE))
                .withChangedProperties(resultSet.getString(SNAPSHOT_CHANGED))
                .withSnapshotType(resultSet.getString(SNAPSHOT_TYPE))
                .withGlobalIdFragment(resultSet.getString(GLOBAL_ID_FRAGMENT))
                .withGlobalIdLocalId(resultSet.getString(GLOBAL_ID_LOCAL_ID))
                .withGlobalIdTypeName(resultSet.getString(SNAPSHOT_MANAGED_TYPE))
                .withOwnerGlobalIdFragment(resultSet.getString("owner_" + GLOBAL_ID_FRAGMENT))
                .withOwnerGlobalIdLocalId(resultSet.getString("owner_" + GLOBAL_ID_LOCAL_ID))
                .withOwnerGlobalIdTypeName(resultSet.getString("owner_" + GLOBAL_ID_TYPE_NAME)),
                resultSet.getLong(COMMIT_PK));
    }
}
