package org.javers.core.json.typeadapter.change;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.common.collections.Lists;
import org.javers.core.diff.changetype.container.*;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class ContainerChangeTypeAdapter extends ChangeTypeAdapter<ContainerChange>{
    private static final String CHANGES_FIELD = "elementChanges";
    private static final String ELEMENT_CHANGE_TYPE_FIELD = "elementChangeType";
    private static final String INDEX_FIELD = "index";
    private static final String VALUE_FIELD = "value";
    private static final String LEFT_VALUE_FIELD = "leftValue";
    private static final String RIGHT_VALUE_FIELD = "rightValue";

    private final TypeMapper typeMapper;

    public ContainerChangeTypeAdapter(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    public JsonElement toJson(ContainerChange change, JsonSerializationContext context) {
        final JsonObject jsonObject = createJsonObject(change, context);

        appendBody(change, jsonObject, context);

        return jsonObject;
    }

    private void appendBody(ContainerChange change, JsonObject toJson, JsonSerializationContext context) {
        JsonArray jsonArray = new JsonArray();

        for (ContainerElementChange elementChange : change.getChanges()) {
            JsonObject jsonElement = new JsonObject();
            jsonElement.addProperty(ELEMENT_CHANGE_TYPE_FIELD, elementChange.getClass().getSimpleName());

            jsonElement.addProperty(INDEX_FIELD, elementChange.getIndex());

            if (elementChange instanceof ValueAddOrRemove) {
                ValueAddOrRemove valueAddOrRemove = (ValueAddOrRemove) elementChange;

                jsonElement.add(VALUE_FIELD, context.serialize(valueAddOrRemove.getValue()));
            }

            if (elementChange instanceof ElementValueChange) {
                ElementValueChange elementValueChange = (ElementValueChange) elementChange;

                jsonElement.add(LEFT_VALUE_FIELD, context.serialize(elementValueChange.getLeftValue()));
                jsonElement.add(RIGHT_VALUE_FIELD, context.serialize(elementValueChange.getRightValue()));
            }
            jsonArray.add(jsonElement);
        }
        toJson.add(CHANGES_FIELD, jsonArray);
    }

    @Override
    public List<Class> getValueTypes() {
        return (List)Lists.immutableListOf(ArrayChange.class, ListChange.class, SetChange.class);
    }
}
