package org.javers.repository.sql.finders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.json.JsonConverter;
import org.javers.core.json.typeadapter.date.DateTypeCoreAdapters;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.polyjdbc.core.query.mapper.ObjectMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

/**
 * @author bartosz.walacik
 */
class CdoSnapshotObjectMapper implements ObjectMapper<CdoSnapshot> {
    private final Optional<GlobalId> providedGlobalId;
    private final JsonConverter jsonConverter;

    private static final String GLOBAL_CDO_ID = "globalId";
    private static final String COMMIT_METADATA = "commitMetadata";
    private static final String STATE_NAME = "state";
    private static final String TYPE_NAME = "type";
    private static final String CHANGED_NAME = "changedProperties";
    private static final String VERSION = "version";


    public CdoSnapshotObjectMapper(JsonConverter jsonConverter, Optional<GlobalId> providedGlobalId) {
        this.jsonConverter = jsonConverter;
        this.providedGlobalId = providedGlobalId;
    }

    @Override
    public CdoSnapshot createObject(ResultSet resultSet) throws SQLException {
        JsonObject json = new JsonObject();

        json.add(COMMIT_METADATA, assembleCommitMetadata(resultSet));
        json.add(STATE_NAME, jsonConverter.fromJsonToJsonElement(resultSet.getString(SNAPSHOT_STATE)));
        json.add(CHANGED_NAME, assembleChangedPropNames(resultSet));
        json.addProperty(TYPE_NAME, resultSet.getString(SNAPSHOT_TYPE));
        json.addProperty(VERSION, resultSet.getLong(VERSION));

        if (providedGlobalId.isPresent()){
            json.add(GLOBAL_CDO_ID, jsonConverter.toJsonElement(providedGlobalId.get()));
        }
        else{
            json.add(GLOBAL_CDO_ID, assembleGlobalId(resultSet));
        }

        return jsonConverter.fromJson(json, CdoSnapshot.class);
    }

    private JsonElement assembleChangedPropNames(ResultSet resultSet) throws SQLException {
        JsonObject jsonObject = new JsonObject();

        String propNamesJSON = resultSet.getString(SNAPSHOT_CHANGED);
        if (propNamesJSON == null || propNamesJSON.isEmpty()){
            return jsonObject;
        }

        return jsonConverter.fromJsonToJsonElement(propNamesJSON);
    }


    private JsonElement assembleCommitMetadata(ResultSet resultSet) throws SQLException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("author",resultSet.getString(COMMIT_AUTHOR));
        jsonObject.addProperty("commitDate", DateTypeCoreAdapters.serializeToLocal( resultSet.getTimestamp(COMMIT_COMMIT_DATE)));
        jsonObject.addProperty("id", resultSet.getBigDecimal(COMMIT_COMMIT_ID));

        return jsonObject;
    }

    private JsonElement assembleGlobalId(ResultSet resultSet){
        try {
            String fragment = resultSet.getString(GLOBAL_ID_FRAGMENT);
            String localIdJSON = resultSet.getString(GLOBAL_ID_LOCAL_ID);
            String cdoType = resultSet.getString(CDO_CLASS_QUALIFIED_NAME);

            JsonObject json = assembleOneGlobalId(cdoType, localIdJSON, fragment);

            if (resultSet.getLong(GLOBAL_ID_OWNER_ID_FK) > 0){

                String ownerFragment = resultSet.getString("owner_"+GLOBAL_ID_FRAGMENT);
                String ownerLocalIdJSON = resultSet.getString("owner_"+GLOBAL_ID_LOCAL_ID);
                String ownerCdoType = resultSet.getString("owner_"+CDO_CLASS_QUALIFIED_NAME);

                JsonObject ownerId = assembleOneGlobalId(ownerCdoType, ownerLocalIdJSON, ownerFragment);
                json.add("ownerId", ownerId);
            }

            return json;

        } catch (SQLException e){
            throw new JaversException(JaversExceptionCode.SQL_EXCEPTION, e.getMessage());
        }
    }

    private JsonObject assembleOneGlobalId(String typeName, String localIdJson, String fragment)
    throws SQLException
    {
        JsonObject json = new JsonObject();
        if (localIdJson != null){
            json.addProperty("entity", typeName);
            json.add("cdoId", jsonConverter.fromJsonToJsonElement(localIdJson));
        }
        else{
            json.addProperty("valueObject", typeName);
            json.addProperty("fragment", fragment);
        }

        return json;
    }
}
