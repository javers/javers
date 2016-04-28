package org.javers.repository.sql.finders;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.core.json.JsonConverter;
import org.javers.core.json.typeadapter.date.DateTypeCoreAdapters;
import org.javers.core.metamodel.object.CdoSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CdoSnapshotsBuilder {
    private JsonConverter jsonConverter;

    private static final String GLOBAL_CDO_ID = "globalId";
    private static final String COMMIT_METADATA = "commitMetadata";
    private static final String STATE_NAME = "state";
    private static final String TYPE_NAME = "type";
    private static final String CHANGED_NAME = "changedProperties";
    private static final String VERSION = "version";

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    public List<CdoSnapshot> buildSnapshots(List<CdoSnapshotDTO> snapshotDTOs, List<CommitPropertyDTO> commitPropertyDTOs) {
        final Map<Long, Map<String, String>> commitsProperties = convertCommitPropertiesToMap(commitPropertyDTOs);
        return Lists.transform(snapshotDTOs, new Function<CdoSnapshotDTO, CdoSnapshot>() {
            @Override
            public CdoSnapshot apply(CdoSnapshotDTO cdoSnapshotDTO) {
                Map<String, String> commitProperties = commitsProperties.get(cdoSnapshotDTO.getCommitPK());

                JsonObject json = new JsonObject();
                json.add(COMMIT_METADATA, assembleCommitMetadata(cdoSnapshotDTO, commitProperties));
                json.add(STATE_NAME, jsonConverter.fromJsonToJsonElement(cdoSnapshotDTO.getSnapshotState()));
                json.add(CHANGED_NAME, assembleChangedPropNames(cdoSnapshotDTO));
                json.addProperty(TYPE_NAME, cdoSnapshotDTO.getSnapshotType());
                json.addProperty(VERSION, cdoSnapshotDTO.getVersion());

                if (cdoSnapshotDTO.getGlobalId() != null){
                    json.add(GLOBAL_CDO_ID, jsonConverter.toJsonElement(cdoSnapshotDTO.getGlobalId()));
                }
                else {
                    json.add(GLOBAL_CDO_ID, assembleGlobalId(cdoSnapshotDTO));
                }

                return jsonConverter.fromJson(json, CdoSnapshot.class);
            }
        });
    }

    private Map<Long, Map<String, String>> convertCommitPropertiesToMap(List<CommitPropertyDTO> commitPropertyDTOs) {
        Map<Long, Map<String, String>> commitsProperties = new HashMap<>();
        for (CommitPropertyDTO commitPropertyDTO : commitPropertyDTOs) {
            if (!commitsProperties.containsKey(commitPropertyDTO.getCommitPK())) {
                commitsProperties.put(commitPropertyDTO.getCommitPK(), new HashMap<String, String>());
            }
            commitsProperties.get(commitPropertyDTO.getCommitPK()).put(commitPropertyDTO.getName(), commitPropertyDTO.getValue());
        }
        return commitsProperties;
    }

    private JsonElement assembleChangedPropNames(CdoSnapshotDTO cdoSnapshotDTO) {
        JsonObject jsonObject = new JsonObject();
        String propNamesJSON = cdoSnapshotDTO.getSnapshotChanged();
        if (propNamesJSON == null || propNamesJSON.isEmpty()){
            return jsonObject;
        }
        return jsonConverter.fromJsonToJsonElement(propNamesJSON);
    }


    private JsonElement assembleCommitMetadata(CdoSnapshotDTO snapshotDTO, Map<String, String> commitProperties) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("author", snapshotDTO.getCommitAuthor());
        jsonObject.add("properties", jsonConverter.toJsonElement(commitProperties));
        jsonObject.addProperty("commitDate", DateTypeCoreAdapters.serializeToLocal(snapshotDTO.getCommitDate()));
        jsonObject.addProperty("id", snapshotDTO.getCommitId());
        return jsonObject;
    }

    private JsonElement assembleGlobalId(CdoSnapshotDTO cdoSnapshotDTO){
        String fragment = cdoSnapshotDTO.getGlobalIdFragment();
        String localIdJSON = cdoSnapshotDTO.getGlobalIdLocalId();
        String cdoType = cdoSnapshotDTO.getSnapshotManagedType();
        String ownerFragment = cdoSnapshotDTO.getOwnerGlobalIdFragment();
        String ownerLocalId = cdoSnapshotDTO.getOwnerGlobalIdLocalId();
        String ownerCdoType = cdoSnapshotDTO.getOwnerGlobalIdTypeName();

        JsonObject json = assembleOneGlobalId(cdoType, localIdJSON, fragment);
        if (ownerFragment != null || ownerLocalId != null || ownerCdoType != null){
            JsonObject ownerId = assembleOneGlobalId(ownerCdoType, ownerLocalId, ownerFragment);
            json.add("ownerId", ownerId);
        }
        return json;
    }

    private JsonObject assembleOneGlobalId(String typeName, String localIdJson, String fragment) {
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
