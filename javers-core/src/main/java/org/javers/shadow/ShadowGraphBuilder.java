package org.javers.shadow;

import com.google.gson.JsonObject;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.ContainerType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;

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
    private boolean built = false;
    private Map<GlobalId, ShadowBuilder> builtNodes = new HashMap<>();
    private final TypeMapper typeMapper;

    ShadowGraphBuilder(JsonConverter jsonConverter, Function<GlobalId, CdoSnapshot> referenceResolver, TypeMapper typeMapper) {
        this.jsonConverter = jsonConverter;
        this.referenceResolver = referenceResolver;
        this.typeMapper = typeMapper;
    }

    Object buildDeepShadow(CdoSnapshot cdoSnapshot) {
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

    private ShadowBuilder assembleShadowStub(CdoSnapshot cdoSnapshot) {
        ShadowBuilder shadowBuilder = new ShadowBuilder(cdoSnapshot);
        builtNodes.put(cdoSnapshot.getGlobalId(), shadowBuilder);

        JsonObject jsonElement = (JsonObject)jsonConverter.toJsonElement(cdoSnapshot.getState());

        extractReferences(shadowBuilder, jsonElement);

        Object shadowStub = jsonConverter.fromJson(jsonElement, cdoSnapshot.getManagedType().getBaseJavaClass());
        shadowBuilder.withStub(shadowStub);

        return shadowBuilder;
    }

    private void extractReferences(ShadowBuilder currentNode, JsonObject jsonElement) {
        CdoSnapshot cdoSnapshot = currentNode.getCdoSnapshot();

        cdoSnapshot.getManagedType().forEachProperty( property -> {
            if (cdoSnapshot.isNull(property)) {
                return;
            }

            if (property.getType() instanceof ManagedType) {
                GlobalId refId = (GlobalId) cdoSnapshot.getPropertyValue(property);

                ShadowBuilder target = createOrReuseNodeFromRef(refId);
                if (target != null) {
                    currentNode.addReferenceWiring(property, target);
                }

                jsonElement.remove(property.getName());
            }

            if (typeMapper.isContainerOfManagedTypes(property.getType()))
            {
                ContainerType propertyType = property.getType();

                Object refIdContainer = cdoSnapshot.getPropertyValue(property);

                currentNode.addReferenceContainerWiring(property, propertyType.mapToList(refIdContainer, this::createOrReuseNodeFromRef));

                jsonElement.remove(property.getName());
            }

        });
    }

    private ShadowBuilder createOrReuseNodeFromRef(Object globalId) {
        return createOrReuseNodeFromRef((GlobalId) globalId);
    }

    private ShadowBuilder createOrReuseNodeFromRef(GlobalId globalId) {
        CdoSnapshot target = referenceResolver.apply(globalId);
        if (target != null) {
            return createOrReuseNode(globalId, target);
        }
        return null;
    }

    private ShadowBuilder createOrReuseNode(GlobalId globalId, CdoSnapshot cdoSnapshot) {
        if (builtNodes.containsKey(globalId)) {
            return builtNodes.get(globalId);
        } else {
            return assembleShadowStub(cdoSnapshot);
        }
    }
}
