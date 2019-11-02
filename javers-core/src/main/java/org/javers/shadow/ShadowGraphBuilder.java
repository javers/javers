package org.javers.shadow;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.CdoSnapshotState;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.type.*;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.javers.core.metamodel.object.CdoSnapshotStateBuilder.cdoSnapshotState;

/**
 * Stateful builder
 *
 * @author bartosz.walacik
 */
class ShadowGraphBuilder {
    private final JsonConverter jsonConverter;
    private final BiFunction<CommitMetadata, GlobalId, CdoSnapshot> referenceResolver;
    private boolean built = false;
    private Map<GlobalId, ShadowBuilder> builtNodes = new HashMap<>();
    private final TypeMapper typeMapper;
    private final CommitMetadata rootContext;

    ShadowGraphBuilder(JsonConverter jsonConverter, BiFunction<CommitMetadata, GlobalId, CdoSnapshot> referenceResolver, TypeMapper typeMapper, CommitMetadata rootContext) {
        this.jsonConverter = jsonConverter;
        this.referenceResolver = referenceResolver;
        this.typeMapper = typeMapper;
        this.rootContext = rootContext;
    }

    Object buildDeepShadow(CdoSnapshot cdoSnapshot) {
        Validate.argumentIsNotNull(cdoSnapshot);
        switchToBuilt();

        ShadowBuilder root = assembleShadowStub(cdoSnapshot);

        doWiring();

        return root.getShadow();
    }

    private void doWiring() {
        builtNodes.values().forEach(ShadowBuilder::wire);
    }

    private void switchToBuilt() {
        if (built) {
            throw new IllegalStateException("already built");
        }
        built = true;
    }

    private ShadowBuilder assembleShallowReferenceShadow(InstanceId instanceId, EntityType shallowReferenceType) {
        CdoSnapshotState state = cdoSnapshotState().withPropertyValue(shallowReferenceType.getIdProperty(), instanceId.getCdoId()).build();

        Object shadowStub = jsonConverter.fromJson(toJson(state), shallowReferenceType.getBaseJavaClass());

        ShadowBuilder shadowBuilder = new ShadowBuilder(null, shadowStub);
        builtNodes.put(instanceId, shadowBuilder);

        return shadowBuilder;
    }

    private ShadowBuilder assembleShadowStub(CdoSnapshot cdoSnapshot) {
        ShadowBuilder shadowBuilder = new ShadowBuilder(cdoSnapshot, null);
        builtNodes.put(cdoSnapshot.getGlobalId(), shadowBuilder);

        JsonObject jsonElement = toJson(cdoSnapshot.getState());
        mapCustomPropertyNamesToJavaOrigin(cdoSnapshot.getManagedType(), jsonElement);
        followReferences(shadowBuilder, jsonElement);

        shadowBuilder.withStub(
                deserializeObjectFromJsonElement(cdoSnapshot.getManagedType(), jsonElement));

        return shadowBuilder;
    }

    private Object deserializeObjectFromJsonElement(ManagedType managedType, JsonObject jsonElement) {
        try {
            return jsonConverter.fromJson(jsonElement, managedType.getBaseJavaClass());
        } catch(JsonSyntaxException | DateTimeParseException e) {
            return sanitizedDeserialization(jsonElement, managedType);
        }
    }

    private Object sanitizedDeserialization(JsonObject jsonElement, ManagedType managedType) {
        managedType.getProperties().forEach(p -> {
            try {
                jsonConverter.fromJson(jsonElement.get(p.getName()), p.getRawType());
            }
            catch (Exception e) {
                jsonElement.remove(p.getName());
            }
        });

        return jsonConverter.fromJson(jsonElement, managedType.getBaseJavaClass());
    }

    private void mapCustomPropertyNamesToJavaOrigin(ManagedType managedType, JsonObject jsonElement) {
        managedType.forEachProperty(javersProperty -> {
                if (javersProperty.hasCustomName()) {
                    JsonElement value = jsonElement.get(javersProperty.getName());
                    jsonElement.remove(javersProperty.getName());
                    jsonElement.add(javersProperty.getOriginalName(), value);
                }
        });
    }

    private void followReferences(ShadowBuilder currentNode, JsonObject jsonElement) {
        CdoSnapshot cdoSnapshot = currentNode.getCdoSnapshot();

        cdoSnapshot.getManagedType().forEachProperty( property -> {
            if (cdoSnapshot.isNull(property)) {
                return;
            }

            if (property.getType() instanceof ManagedType) {
                GlobalId refId = (GlobalId) cdoSnapshot.getPropertyValue(property);

                ShadowBuilder target = createOrReuseNodeFromRef(refId, property);
                if (target != null) {
                    currentNode.addReferenceWiring(property, target);
                }

                jsonElement.remove(property.getName());
            }

            if (typeMapper.isContainerOfManagedTypes(property.getType()) ||
                typeMapper.isKeyValueTypeWithManagedTypes(property.getType()))
            {
                EnumerableType propertyType = property.getType();

                Object containerWithRefs = cdoSnapshot.getPropertyValue(property);
                if (!propertyType.isEmpty(containerWithRefs)) {
                    currentNode.addEnumerableWiring(property, propertyType
                               .map(containerWithRefs, (value) -> passValueOrCreateNodeFromRef(value, property), true));
                    jsonElement.remove(property.getName());
                }
            }
        });
    }

    private Object passValueOrCreateNodeFromRef(Object value, JaversProperty property) {
        if (value instanceof GlobalId) {
            return createOrReuseNodeFromRef((GlobalId)value, property);
        }
        return value;
    }

    private ShadowBuilder createOrReuseNodeFromRef(GlobalId globalId, JaversProperty property) {
        if (builtNodes.containsKey(globalId)) {
            return builtNodes.get(globalId);
        }

        if (property.isShallowReference()) {
            EntityType shallowReferenceType = property.getType() instanceof EntityType
                    ? property.getType()
                    : (EntityType)typeMapper.getJaversManagedType(globalId);

            if (shallowReferenceType.getIdProperty().getType() instanceof ValueObjectType) {
                //TODO don't know how to reconstruct Id in ShallowReference, which happened to be a ValueObject,
                return null;
            } else {
                return assembleShallowReferenceShadow((InstanceId) globalId, shallowReferenceType);
            }
        }

        CdoSnapshot cdoSnapshot = referenceResolver.apply(rootContext, globalId);
        if (cdoSnapshot != null) {
            return assembleShadowStub(cdoSnapshot);
        }
        return null;
    }

    private JsonObject toJson(CdoSnapshotState state) {
        return (JsonObject)jsonConverter.toJsonElement(state);
    }
}
