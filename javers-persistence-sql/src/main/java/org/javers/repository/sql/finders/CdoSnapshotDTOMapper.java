package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.core.metamodel.object.GlobalId;
import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author bartosz.walacik
 */
class CdoSnapshotDTOMapper implements ObjectMapper<CdoSnapshotDTO> {
    private final Optional<GlobalId> providedGlobalId;

    public CdoSnapshotDTOMapper(Optional<GlobalId> providedGlobalId) {
        this.providedGlobalId = providedGlobalId;
    }

    @Override
    public CdoSnapshotDTO createObject(ResultSet resultSet) throws SQLException {
        if (providedGlobalId.isPresent()) {
            return new CdoSnapshotDTO(
                resultSet.getLong(COMMIT_PK),
                resultSet.getString(COMMIT_AUTHOR),
                resultSet.getTimestamp(COMMIT_COMMIT_DATE),
                resultSet.getBigDecimal(COMMIT_COMMIT_ID),
                resultSet.getLong(SNAPSHOT_VERSION),
                resultSet.getString(SNAPSHOT_STATE),
                resultSet.getString(SNAPSHOT_CHANGED),
                resultSet.getString(SNAPSHOT_TYPE),
                resultSet.getString(SNAPSHOT_MANAGED_TYPE),
                providedGlobalId.get()
            );
        }
        else {
            return new CdoSnapshotDTO(
                resultSet.getLong(COMMIT_PK),
                resultSet.getString(COMMIT_AUTHOR),
                resultSet.getTimestamp(COMMIT_COMMIT_DATE),
                resultSet.getBigDecimal(COMMIT_COMMIT_ID),
                resultSet.getLong(SNAPSHOT_VERSION),
                resultSet.getString(SNAPSHOT_STATE),
                resultSet.getString(SNAPSHOT_CHANGED),
                resultSet.getString(SNAPSHOT_TYPE),
                resultSet.getString(SNAPSHOT_MANAGED_TYPE),
                resultSet.getString(GLOBAL_ID_FRAGMENT),
                resultSet.getString(GLOBAL_ID_LOCAL_ID),
                resultSet.getString("owner_" + GLOBAL_ID_FRAGMENT),
                resultSet.getString("owner_" + GLOBAL_ID_LOCAL_ID),
                resultSet.getString("owner_" + GLOBAL_ID_TYPE_NAME)
            );
        }
    }

}
