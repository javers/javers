package org.javers.core.json.typeadapter.change;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.common.collections.Lists;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.InitialValueChange;
import org.javers.core.diff.changetype.PropertyChangeMetadata;
import org.javers.core.diff.changetype.TerminalValueChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;

class ValueChangeTypeAdapter extends ChangeTypeAdapter<ValueChange> {
    private static final String LEFT_VALUE_FIELD = "left";
    private static final String RIGHT_VALUE_FIELD = "right";

    public ValueChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public ValueChange fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        PropertyChangeMetadata stub = deserializeStub(jsonObject, context);

        Object leftValue  = context.deserialize(jsonObject.get(LEFT_VALUE_FIELD),  getJaversProperty(stub).getGenericType());
        Object rightValue = context.deserialize(jsonObject.get(RIGHT_VALUE_FIELD), getJaversProperty(stub).getGenericType());

        Class<? extends Change> changeType = decodeChangeType((JsonObject) json);

        if (changeType == ValueChange.class) {
            return new ValueChange(stub, leftValue, rightValue);
        }
        if (changeType == InitialValueChange.class) {
            return new InitialValueChange(stub, rightValue);
        }
        if (changeType == TerminalValueChange.class) {
            return new TerminalValueChange(stub, leftValue);
        }
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
    }

    @Override
    public JsonElement toJson(ValueChange change, JsonSerializationContext context) {
        JsonObject jsonObject = createJsonObject(change, context);

        jsonObject.add(LEFT_VALUE_FIELD, context.serialize(change.getLeft()));
        jsonObject.add(RIGHT_VALUE_FIELD, context.serialize(change.getRight()));

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
    }

    @Override
    public List<Class> getValueTypes() {
        return Lists.asList(ValueChange.class, InitialValueChange.class, TerminalValueChange.class);
    }
}
