package org.javers.core.graph;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.javers.common.string.ShaDigest;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshotState;
import org.javers.core.metamodel.type.CollectionType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ListAsSetType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.SetType;
import org.javers.core.metamodel.type.ValueObjectType;
import org.javers.core.snapshot.SnapshotFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class SnapshotObjectHasher implements ObjectHasher {
	
	private final SnapshotFactory snapshotFactory;
    private final JsonConverter jsonConverter;

    SnapshotObjectHasher(SnapshotFactory snapshotFactory, JsonConverter jsonConverter) {
        this.snapshotFactory = snapshotFactory;
        this.jsonConverter = jsonConverter;
    }

    @Override
    public String hash(List<LiveCdo> objects) {
        String jsonState = objects.stream()
                                  .map(this::toCanonicalJson)
                                  .sorted()
                                  .collect(Collectors.joining("\n"));

        return ShaDigest.longDigest(jsonState);
    }

    /**
     * Converts the given liveObject to its canonical JSON representation.
     *
     * @param liveObject the live liveObject to convert
     * @return the canonical JSON string
     */
    private String toCanonicalJson(LiveCdo liveObject) {
        CdoSnapshotState state = snapshotFactory.createSnapshotStateNoRefs(liveObject);
        JsonObject jsonObject = jsonConverter.toJsonElement(state).getAsJsonObject();
        canonicalize(jsonObject, liveObject.getManagedType());
        return jsonConverter.toJson(jsonObject);
    }

    /**
     * Recursively canonicalizes a JSON object representing a Javers-managed type,
     * so that semantically equal objects produce identical JSON regardless of
     * collection ordering.
     * <p>
     * For each property of {@code managedType}:
     * <ul>
     *   <li>if it's a {@link SetType}, recurses into {@link ValueObjectType} elements
     *       (bottom-up) then sorts the resulting array by element string representation;</li>
     *   <li>if it's a {@link ValueObjectType}, recurses into the nested object.</li>
     * </ul>
     *
     * @param node the JSON object to canonicalize, mutated in place
     * @param managedType the Javers metadata describing {@code node}'s properties
     */
    private void canonicalize(JsonObject node, ManagedType managedType) {
        for(JaversProperty property :  managedType.getProperties()) {
            JsonElement jsonField = node.get(property.getName());
            if (jsonField == null) {
                continue;
            }

            JaversType propertyType = property.getType();

            if (propertyType instanceof SetType || propertyType instanceof ListAsSetType) {
                JsonArray array = jsonField.getAsJsonArray();
                JaversType elementType = ((CollectionType) propertyType).getItemJaversType();

                if (elementType instanceof ValueObjectType) {
                    // first, recurse into each element (bottom-up)
                    array.forEach(element -> canonicalize(element.getAsJsonObject(), (ValueObjectType) elementType));
                }

                // then sort the array by element.toString()
                sortJsonArray(jsonField.getAsJsonArray());
            }
            else if (propertyType instanceof ValueObjectType){
                // recurse into nested ValueObjectType
                canonicalize(jsonField.getAsJsonObject(), (ValueObjectType) propertyType);
            }
        }
    }

    /**
     * Sorts the elements of a {@link JsonArray} in place, ordering them
     * lexicographically by their string representation ({@link JsonElement#toString()}).
     *
     * @param jsonArray the JSON array to sort in place
     */
    private void sortJsonArray(JsonArray jsonArray) {
        jsonArray.asList().sort(Comparator.comparing(JsonElement::toString));
    }

}
