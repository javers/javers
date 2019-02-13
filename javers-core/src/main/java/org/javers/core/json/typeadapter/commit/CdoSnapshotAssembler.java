package org.javers.core.json.typeadapter.commit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.javers.core.json.CdoSnapshotSerialized;
import org.javers.core.json.JsonConverter;

import static org.javers.core.json.typeadapter.commit.CdoSnapshotTypeAdapter.*;
import static org.javers.core.json.typeadapter.commit.CommitMetadataTypeAdapter.*;
import static org.javers.core.json.typeadapter.commit.GlobalIdTypeAdapter.*;

public class CdoSnapshotAssembler {
    private final JsonConverter jsonConverter;

    public CdoSnapshotAssembler(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    public JsonElement assemble(CdoSnapshotSerialized snapshot) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.add(COMMIT_METADATA, assembleCommitMetadata(snapshot));
        jsonObject.add(STATE_NAME, jsonConverter.fromJsonToJsonElement(snapshot.getSnapshotState()));
        jsonObject.add(CHANGED_NAME, assembleChangedPropNames(snapshot));
        jsonObject.addProperty(TYPE_NAME, snapshot.getSnapshotType());
        jsonObject.addProperty(VERSION, snapshot.getVersion());
        jsonObject.add(GLOBAL_CDO_ID, assembleGlobalId(snapshot));

        return jsonObject;
    }

    private JsonElement assembleGlobalId(CdoSnapshotSerialized snapshot){
        String fragment = snapshot.getGlobalIdFragment();
        String localIdJSON = snapshot.getGlobalIdLocalId();
        String cdoType = snapshot.getGlobalIdTypeName();
        String ownerFragment = snapshot.getOwnerGlobalIdFragment();
        String ownerLocalId = snapshot.getOwnerGlobalIdLocalId();
        String ownerCdoType = snapshot.getOwnerGlobalIdTypeName();

        JsonObject json = assembleOneGlobalId(cdoType, localIdJSON, fragment);
        if (ownerFragment != null || ownerLocalId != null || ownerCdoType != null){
            JsonObject ownerId = assembleOneGlobalId(ownerCdoType, ownerLocalId, ownerFragment);
            json.add(OWNER_ID_FIELD, ownerId);
        }
        return json;
    }

    private JsonObject assembleOneGlobalId(String typeName, String localIdJson, String fragment) {
        JsonObject json = new JsonObject();
        if (localIdJson != null){
            json.addProperty(ENTITY_FIELD, typeName);
            json.add(CDO_ID_FIELD, jsonConverter.fromJsonToJsonElement(localIdJson));
        }
        else{
            json.addProperty(VALUE_OBJECT_FIELD, typeName);
            json.addProperty(FRAGMENT_FIELD, fragment);
        }
        return json;
    }

    private JsonElement assembleChangedPropNames(CdoSnapshotSerialized snapshot) {
        String changed = snapshot.getChangedProperties();
        if (changed == null || changed.isEmpty()){
            return new JsonObject();
        }
        return jsonConverter.fromJsonToJsonElement(changed);
    }

    private JsonElement assembleCommitMetadata(CdoSnapshotSerialized snapshot) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(AUTHOR, snapshot.getCommitAuthor());
        jsonObject.add(PROPERTIES, CommitPropertiesConverter.toJson(snapshot.getCommitProperties()));
        jsonObject.add(COMMIT_DATE, jsonConverter.toJsonElement(snapshot.getCommitDate()));
        jsonObject.add(COMMIT_DATE_INSTANT, jsonConverter.toJsonElement(snapshot.getCommitDateInstant()));
        jsonObject.add(COMMIT_ID, jsonConverter.toJsonElement(snapshot.getCommitId()));

        return jsonObject;
    }
}
