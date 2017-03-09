package org.javers.shadow;

import com.google.gson.JsonObject;
import org.javers.common.validation.Validate;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Stateful builder
 *
 * @author bartosz.walacik
 */
class ShadowGraphBuilder {
    private final JsonConverter jsonConverter;
    private final Function<GlobalId, CdoSnapshot> referenceResolver;
    private final TypeMapper typeMapper;
    private boolean built = false;

    ShadowGraphBuilder(JsonConverter jsonConverter, Function<GlobalId, CdoSnapshot> referenceResolver, TypeMapper typeMapper) {
        this.jsonConverter = jsonConverter;
        this.referenceResolver = referenceResolver;
        this.typeMapper = typeMapper;
    }

    Object buildDeepShadow(CdoSnapshot cdoSnapshot) {
        Validate.argumentIsNotNull(cdoSnapshot);
        switchToBuilt();

        Collection<ShadowBuilder> shadowBuilders = createStubShadowBuildersOfCdoSnapshotssAchievableFrom(cdoSnapshot);
        wireStubShadowBuilders(shadowBuilders);
        return findShadowBuilderForSnapshot(shadowBuilders, cdoSnapshot).getShadow();
    }

    private void wireStubShadowBuilders(Collection<ShadowBuilder> shadowBuilders) {
        shadowBuilders.forEach(ShadowBuilder::wire);
    }

    private ShadowBuilder findShadowBuilderForSnapshot(Collection<ShadowBuilder> shadowBuilders, CdoSnapshot cdoSnapshot) {
        return shadowBuilders.stream().filter(b -> cdoSnapshot.equals(b.getCdoSnapshot())).findFirst().get();
    }

    private void switchToBuilt() {
        if (built) {
            throw new IllegalStateException("already built");
        }
        built = true;
    }

    private Collection<ShadowBuilder> createStubShadowBuildersOfCdoSnapshotssAchievableFrom(CdoSnapshot cdoSnapshot) {
        Map<GlobalId, ShadowBuilder> builtNodes = new HashMap<>();
        assembleShadowStub(cdoSnapshot, builtNodes);
        return builtNodes.values();
    }

    private ShadowBuilder assembleShadowStub(CdoSnapshot cdoSnapshot, Map<GlobalId, ShadowBuilder> builtNodes) {
        ShadowBuilder shadowBuilder = new ShadowBuilder(cdoSnapshot);
        builtNodes.put(cdoSnapshot.getGlobalId(), shadowBuilder);

        JsonObject jsonElement = (JsonObject)jsonConverter.toJsonElement(cdoSnapshot.getState());

        followReferences(shadowBuilder, jsonElement, builtNodes);

        Object shadowStub = jsonConverter.fromJson(jsonElement, cdoSnapshot.getManagedType().getBaseJavaClass());
        shadowBuilder.withStub(shadowStub);

        return shadowBuilder;
    }

    private void followReferences(ShadowBuilder currentNode, JsonObject jsonElement, Map<GlobalId, ShadowBuilder> builtNodes) {
        CdoSnapshot cdoSnapshot = currentNode.getCdoSnapshot();

        cdoSnapshot.getManagedType().forEachProperty( property -> {
            if (cdoSnapshot.isNull(property)) {
                return;
            }

            if (property.getType() instanceof ManagedType) {
                GlobalId refId = (GlobalId) cdoSnapshot.getPropertyValue(property);

                ShadowBuilder target = createOrReuseNodeFromRef(refId, builtNodes);
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

                currentNode.addEnumerableWiring(property,
                        propertyType.map(containerWithRefs, (o -> passValueOrCreateNodeFromRef(o, builtNodes))));

                jsonElement.remove(property.getName());
            }
        });
    }

    private Object passValueOrCreateNodeFromRef(Object value, Map<GlobalId, ShadowBuilder> builtNodes) {
        if (value instanceof GlobalId) {
            return createOrReuseNodeFromRef((GlobalId)value, builtNodes);
        }
        return value;
    }

    private ShadowBuilder createOrReuseNodeFromRef(GlobalId globalId, Map<GlobalId, ShadowBuilder> builtNodes) {
        CdoSnapshot cdoSnapshot = referenceResolver.apply(globalId);
        if (cdoSnapshot != null) {
            if (builtNodes.containsKey(globalId)) {
                return builtNodes.get(globalId);
            } else {
                return assembleShadowStub(cdoSnapshot, builtNodes);
            }
        }
        return null;
    }
}
