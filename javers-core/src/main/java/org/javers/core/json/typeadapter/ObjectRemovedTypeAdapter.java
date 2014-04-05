package org.javers.core.json.typeadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.javers.core.diff.changetype.ObjectRemoved;

public class ObjectRemovedTypeAdapter extends AbstractTypeAdapter<ObjectRemoved> {

  @Override
  public JsonElement toJson(ObjectRemoved change, JsonSerializationContext context) {
    return createJsonObject(change, context);
  }

}
