package org.javers.core.json.typeadapter;

import com.google.gson.*;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.property.Entity;

import java.lang.reflect.ParameterizedType;

public abstract class AbstractTypeAdapter<T extends Change> implements JsonTypeAdapter<T> {

  @Override
  public T fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
    throw new IllegalStateException("not implemented");
  }

  @Override
  public Class getValueType() {
    // FIXME - dirty hack
    return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  protected JsonObject createJsonObject(Change change, JsonSerializationContext context) {
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("changeType", change.getClass().getSimpleName());
    jsonObject.add("globalCdoId", globalCdoId(change.getGlobalCdoId(), context));

    if (change instanceof PropertyChange) {
      jsonObject.addProperty("property", ((PropertyChange) change).getProperty().getName());
    }

    return jsonObject;
  }

  protected JsonElement globalCdoId(GlobalCdoId globalCdoId, JsonSerializationContext context) {
    if (globalCdoId == null) {
      return JsonNull.INSTANCE;
    }
    JsonObject jsonObject = new JsonObject();

    //managedClass
    if (globalCdoId.getCdoClass() instanceof Entity) {
      jsonObject.addProperty("entity", globalCdoId.getCdoClass().getName());
    } else {
      jsonObject.addProperty("valueObject", globalCdoId.getCdoClass().getName());
    }

    //cdoId
    if (globalCdoId.getCdoId() != null) {
      jsonObject.add("cdoId", context.serialize(globalCdoId.getCdoId()));
    }

    //owningId & fragment
    if (globalCdoId instanceof ValueObjectId) {
      ValueObjectId valueObjectId = (ValueObjectId) globalCdoId;
      jsonObject.add("ownerId", globalCdoId(valueObjectId.getOwnerId(), context));
      jsonObject.addProperty("fragment", valueObjectId.getFragment());
    }

    return jsonObject;
  }
}
