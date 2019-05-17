package org.javers.core.json.typeadapter.commit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffBuilder;

import java.lang.reflect.Type;
import java.util.List;

public class DiffTypeDeserializer implements JsonDeserializer<Diff> {
    private static final String CHANGES_FIELD = "changes";

    @Override
    public Diff deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement changesObject = ((JsonObject)json).get(CHANGES_FIELD);

        if (changesObject != null) {
            List<Change> changes = context.deserialize(changesObject, new TypeToken<List<Change>>(){}.getType());
            return new DiffBuilder()
                    .addChanges(changes)
                    .build();
        }
        return DiffBuilder.empty();
    }
}
