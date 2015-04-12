package org.javers.repository.sql.finders;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.GlobalIdRawDTO;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.*;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * Created by bartosz.walacik on 2015-04-11.
 */
class CdoSnapshotObjectMapper implements ObjectMapper<CdoSnapshot> {
    private final GlobalId providedGlobalId;
    private final JsonConverter jsonConverter;

    public CdoSnapshotObjectMapper(JsonConverter jsonConverter, GlobalId providedGlobalId) {
        this.jsonConverter = jsonConverter;
        this.providedGlobalId = providedGlobalId;
    }

    @Override
    public CdoSnapshot createObject(ResultSet resultSet) throws SQLException {
        GlobalId usedGlobalId;
        if (providedGlobalId != null){
            usedGlobalId = providedGlobalId;
        }
        else{
            GlobalIdRawDTO globalIdRawDTO = fromResultSet(resultSet,"");
            usedGlobalId = jsonConverter.fromDto(globalIdRawDTO);
        }

        String author = resultSet.getString(COMMIT_AUTHOR);
        LocalDateTime commitDate = new LocalDateTime(resultSet.getTimestamp(COMMIT_COMMIT_DATE));
        CommitId commitId = CommitId.valueOf(resultSet.getString(COMMIT_COMMIT_ID));
        CommitMetadata commit = new CommitMetadata(author, commitDate, commitId);

        SnapshotType snapshotType = SnapshotType.valueOf(resultSet.getString(SNAPSHOT_TYPE));
        CdoSnapshotState state = jsonConverter.snapshotStateFromJson(resultSet.getString(SNAPSHOT_STATE), usedGlobalId); //ManagedClass?
        CdoSnapshotBuilder builder = CdoSnapshotBuilder.cdoSnapshot(usedGlobalId, commit);
        builder.withType(snapshotType);
        return builder.withState(state).build();
    }

    public GlobalIdRawDTO fromResultSet(ResultSet resultSet, String prefix){
        if (prefix == null){
            prefix = "";
        }
        try {
            String fragment = resultSet.getString(prefix+GLOBAL_ID_FRAGMENT);
            String localIdJSON = resultSet.getString(prefix+GLOBAL_ID_LOCAL_ID);
            String cdoClass = resultSet.getString(prefix+CDO_CLASS_QUALIFIED_NAME);
            GlobalIdRawDTO ownerId = null;
            if (resultSet.getLong(prefix+GLOBAL_ID_OWNER_ID_FK) > 0){
                ownerId = fromResultSet(resultSet, "owner_");
            }

            return new GlobalIdRawDTO(cdoClass, localIdJSON, fragment, ownerId);
        } catch (SQLException e){
            throw new JaversException(JaversExceptionCode.SQL_EXCEPTION, e.getMessage());
        }
    }
}
