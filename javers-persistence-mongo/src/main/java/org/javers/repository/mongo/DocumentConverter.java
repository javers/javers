package org.javers.repository.mongo;

import com.google.gson.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author bartosz.walacik
 */
class DocumentConverter {
    static Document toDocument(JsonObject jsonObject) {
        Document document = new Document();

        for(Map.Entry<String,JsonElement> e : jsonObject.entrySet()) {
            document.append(e.getKey(), fromJsonElement(e.getValue()));
        }
        return document;
    }

    static JsonElement fromDocument(Document document) {
        JsonObject jsonObject = new JsonObject();
        for(Map.Entry<String,Object> e : document.entrySet()) {
            jsonObject.add(e.getKey(), createJsonElement(e.getValue()) );
        }
        return jsonObject;
    }

    private static Object fromJsonElement(JsonElement jsonElement) {
        if (jsonElement == JsonNull.INSTANCE) {
            return null;
        }

        if (jsonElement instanceof JsonObject) {
            return toDocument((JsonObject) jsonElement);
        }

        if (jsonElement instanceof JsonPrimitive) {
            JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;

            if (jsonPrimitive.isString()) {
                return jsonElement.getAsString();
            }

            if (jsonPrimitive.isNumber() && jsonElement.getAsNumber() instanceof BigDecimal) {
                BigDecimal value = ((BigDecimal) jsonElement.getAsNumber());
                try {
                    return value.longValueExact();
                } catch (ArithmeticException e){
                    return value.doubleValue();
                }
            }

            if (jsonPrimitive.isNumber()) {
                return jsonElement.getAsNumber();
            }

            if (jsonPrimitive.isBoolean()) {
                return jsonElement.getAsBoolean();
            }
        }

        if (jsonElement instanceof JsonArray) {
            List list = new ArrayList();
            for (JsonElement e : ((JsonArray)jsonElement)) {
                list.add(fromJsonElement(e));
            }
            return list;
        }

        throw new IllegalArgumentException("unsupported JsonElement type - " + jsonElement.getClass().getSimpleName());
    }

    private static JsonElement createJsonElement(Object dbObject) {
        if (dbObject == null) {
            return JsonNull.INSTANCE;
        }

        if (dbObject instanceof Document) {
            return fromDocument((Document) dbObject);
        }

        if (dbObject instanceof String) {
            return new JsonPrimitive((String)dbObject);
        }

        if (dbObject instanceof Number) {
            return new JsonPrimitive((Number)dbObject);
        }

        if (dbObject instanceof Boolean) {
            return new JsonPrimitive((Boolean) dbObject);
        }

        if (dbObject instanceof List) {
            JsonArray array = new JsonArray();
            for (Object e : (List) dbObject) {
                array.add(createJsonElement(e));
            }
            return array;
        }

        if (dbObject instanceof ObjectId) {
            JsonObject id = new JsonObject();
            id.addProperty("$oid",dbObject.toString());
            return id;
        }

        throw new IllegalArgumentException("unsupported dbObject type - " + dbObject.getClass().getSimpleName());
    }
}
