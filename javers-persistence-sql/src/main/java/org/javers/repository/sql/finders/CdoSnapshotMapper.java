package org.javers.repository.sql.finders;

import org.javers.common.collections.Pair;
import org.javers.core.json.CdoSnapshotSerialized;

import java.sql.Clob;
import java.time.LocalDateTime;
import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

class CdoSnapshotMapper implements ObjectMapper<Pair<CdoSnapshotSerialized,Long>> {

    @Override
    public Pair<CdoSnapshotSerialized,Long> createObject(ResultSet resultSet) throws SQLException {
        Clob stateClob = resultSet.getClob(SNAPSHOT_STATE);
        Clob changedClob = resultSet.getClob(SNAPSHOT_CHANGED);
        return new Pair<>(new CdoSnapshotSerialized()
                .withCommitAuthor(resultSet.getString(COMMIT_AUTHOR))
                .withCommitDate(resultSet.getTimestamp(COMMIT_COMMIT_DATE))
                .withCommitId(resultSet.getBigDecimal(COMMIT_COMMIT_ID))
                .withVersion(resultSet.getLong(SNAPSHOT_VERSION))
                .withSnapshotState(stateClob.getSubString(1L, (int) stateClob.length()))
                .withChangedProperties(changedClob.getSubString(1L, (int) changedClob.length()))
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
