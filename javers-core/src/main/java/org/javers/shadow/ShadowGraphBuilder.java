package org.javers.shadow;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.type.*;

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
        JsonObject jsonElement = (JsonObject)jsonConverter.toJsonElement(state);
        Object shadowStub = jsonConverter.fromJson(jsonElement, shallowReferenceType.getBaseJavaClass());

        ShadowBuilder shadowBuilder = new ShadowBuilder(null, shadowStub);
        builtNodes.put(instanceId, shadowBuilder);

        return shadowBuilder;
    }

    private ShadowBuilder assembleShadowStub(CdoSnapshot cdoSnapshot) {
        ShadowBuilder shadowBuilder = new ShadowBuilder(cdoSnapshot, null);
        builtNodes.put(cdoSnapshot.getGlobalId(), shadowBuilder);

        JsonObject jsonElement = (JsonObject)jsonConverter.toJsonElement(cdoSnapshot.getState());
        mapCustomPropertyNamesToJavaOrigin(cdoSnapshot, jsonElement);
        followReferences(shadowBuilder, jsonElement);

        Object shadowStub = jsonConverter.fromJson(jsonElement, cdoSnapshot.getManagedType().getBaseJavaClass());
        shadowBuilder.withStub(shadowStub);

        return shadowBuilder;
    }

    private void mapCustomPropertyNamesToJavaOrigin(CdoSnapshot cdoSnapshot, JsonObject jsonElement) {
        cdoSnapshot.getManagedType().forEachProperty(javersProperty -> {
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
                    currentNode.addEnumerableWiring(property, propertyType.map(containerWithRefs, (value) -> passValueOrCreateNodeFromRef(value, property)));
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
            return assembleShallowReferenceShadow((InstanceId)globalId, (EntityType)property.getType());
        }

        CdoSnapshot cdoSnapshot = referenceResolver.apply(rootContext, globalId);
        if (cdoSnapshot != null) {
            return assembleShadowStub(cdoSnapshot);
        }
        return null;
    }
}
