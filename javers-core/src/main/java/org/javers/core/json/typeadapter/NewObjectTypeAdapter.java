package org.javers.core.json.typeadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.javers.core.diff.changetype.NewObject;

public class NewObjectTypeAdapter extends AbstractTypeAdapter<NewObject> {

  @Override
  public JsonElement toJson(NewObject change, JsonSerializationContext context) {
    return createJsonObject(change, context);
  }
}
