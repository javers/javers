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
import java.util.Collections;
import java.util.List;

import static org.javers.core.metamodel.object.SnapshotType.valueOf;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author bartosz.walacik
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
            GlobalIdRawDTO globalIdRawDTO = assembleGlobalIdRawDTO(resultSet);
            usedGlobalId = jsonConverter.fromDto(globalIdRawDTO);
        }

        CommitMetadata commit = assembleCommitMetadata(resultSet);
        CdoSnapshotState state = jsonConverter.snapshotStateFromJson(resultSet.getString(SNAPSHOT_STATE), usedGlobalId);
        List<String> changedPropNames = assembleChangedPropNames(resultSet);

        CdoSnapshotBuilder builder = CdoSnapshotBuilder.cdoSnapshot(usedGlobalId, commit);
        builder.withType(valueOf(resultSet.getString(SNAPSHOT_TYPE)));
        builder.withChangedProperties(changedPropNames);

        return builder.withState(state).build();
    }

    private List<String> assembleChangedPropNames(ResultSet resultSet) throws SQLException {
        String propNamesJSON = resultSet.getString(SNAPSHOT_CHANGED);
        if (propNamesJSON == null || propNamesJSON.isEmpty()){
            return Collections.emptyList();
        }

        return jsonConverter.fromJson(propNamesJSON, List.class);
    }


    private CommitMetadata assembleCommitMetadata(ResultSet resultSet) throws SQLException {
        String author = resultSet.getString(COMMIT_AUTHOR);
        LocalDateTime commitDate = new LocalDateTime(resultSet.getTimestamp(COMMIT_COMMIT_DATE));
        CommitId commitId = CommitId.valueOf(resultSet.getString(COMMIT_COMMIT_ID));
        return new CommitMetadata(author, commitDate, commitId);
    }

    private GlobalIdRawDTO assembleGlobalIdRawDTO(ResultSet resultSet){

        try {
            String fragment = resultSet.getString(GLOBAL_ID_FRAGMENT);
            String localIdJSON = resultSet.getString(GLOBAL_ID_LOCAL_ID);
            String cdoClass = resultSet.getString(CDO_CLASS_QUALIFIED_NAME);

            GlobalIdRawDTO ownerId = null;
            if (resultSet.getLong(GLOBAL_ID_OWNER_ID_FK) > 0){

                String ownerFragment = resultSet.getString("owner_"+GLOBAL_ID_FRAGMENT);
                String ownerLocalIdJSON = resultSet.getString("owner_"+GLOBAL_ID_LOCAL_ID);
                String ownerCdoClass = resultSet.getString("owner_"+CDO_CLASS_QUALIFIED_NAME);

                ownerId = new GlobalIdRawDTO(ownerCdoClass, ownerLocalIdJSON, ownerFragment, null);
            }

            return new GlobalIdRawDTO(cdoClass, localIdJSON, fragment, ownerId);
        } catch (SQLException e){
            throw new JaversException(JaversExceptionCode.SQL_EXCEPTION, e.getMessage());
        }
    }
}
