package org.javers.core.json.typeadapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import org.javers.core.diff.changetype.PropertyChange;

public class PropertyChangeTypeAdapter extends AbstractTypeAdapter<PropertyChange> {

  @Override
  public JsonElement toJson(PropertyChange change, JsonSerializationContext context) {
    return createJsonObject(change, context);
  }

}
