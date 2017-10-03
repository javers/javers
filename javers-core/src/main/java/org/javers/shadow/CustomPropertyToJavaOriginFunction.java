package org.javers.shadow;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.javers.common.collections.Consumer;
import org.javers.core.metamodel.type.JaversProperty;

/**
 * @author pawel szymczyk
 */
class CustomPropertyToJavaOriginFunction implements Consumer<JaversProperty> {

    private final JsonObject jsonElement;

    public CustomPropertyToJavaOriginFunction(JsonObject jsonElement) {
        this.jsonElement = jsonElement;
    }

    @Override
    public void consume(JaversProperty javersProperty) {
        if (javersProperty.isCustomPropertyName()) {
            changePropertyNameToJavaOrigin(javersProperty);
        }
    }

    private void changePropertyNameToJavaOrigin(JaversProperty javersProperty) {
        JsonElement value = jsonElement.get(javersProperty.getName());
        jsonElement.remove(javersProperty.getName());
        jsonElement.add(javersProperty.getOriginName(), value);
    }
}
