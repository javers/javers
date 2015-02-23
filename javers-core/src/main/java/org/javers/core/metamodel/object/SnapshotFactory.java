package org.javers.core.metamodel.object;

import org.javers.common.collections.Defaults;
import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Objects;
import org.javers.common.exception.JaversException;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

import static org.javers.common.exception.JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED;
import static org.javers.common.exception.JaversExceptionCode.NOT_IMPLEMENTED;
import static org.javers.core.metamodel.object.CdoSnapshotBuilder.cdoSnapshot;
import static org.javers.core.metamodel.object.CdoSnapshotStateBuilder.cdoSnapshotState;
import static org.javers.core.metamodel.object.SnapshotType.*;

/**
 * @author bartosz walacik
 */
public class SnapshotFactory {
    private final TypeMapper typeMapper;
    private final GlobalIdFactory globalIdFactory;

    public SnapshotFactory(TypeMapper typeMapper, GlobalIdFactory globalIdFactory) {
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;
    }

    /**
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED
     */
    CdoSnapshot create(Object liveCdo, GlobalId id, CommitMetadata commitMetadata, SnapshotType type) {
        CdoSnapshotBuilder snapshot = cdoSnapshot(id, commitMetadata).withType(type);
        CdoSnapshotStateBuilder stateBuilder = cdoSnapshotState();

        for (Property property : id.getCdoClass().getProperties()) {
            Object propertyVal = property.get(liveCdo);
            if (Objects.nullSafeEquals(propertyVal, Defaults.defaultValue(property.getType()))) {
                continue;
            }

            JaversType propertyType = typeMapper.getPropertyType(property);
            OwnerContext owner = new OwnerContext(id, property.getName());

            Object filteredPropertyVal;
            if (propertyType instanceof EnumerableType) {
                filteredPropertyVal = extractAndDehydrateEnumerable(propertyVal, (EnumerableType) propertyType, owner);
            } else {
                filteredPropertyVal = globalIdFactory.dehydrate(propertyVal, propertyType, owner);
            }

            stateBuilder.withPropertyValue(property, filteredPropertyVal);
        }

        return snapshot.withState(stateBuilder.build()).build();
    }

    public CdoSnapshot createTerminal(GlobalId globalId, CommitMetadata commitMetadata) {
        return cdoSnapshot(globalId, commitMetadata).withType(TERMINAL).build();
    }

    public CdoSnapshot createInitial(ObjectNode objectNode, CommitMetadata commitMetadata) {
        return create(objectNode.wrappedCdo().get(), objectNode.getGlobalId(), commitMetadata, INITIAL);
    }

    public CdoSnapshot create(ObjectNode objectNode, GlobalId globalId, CommitMetadata commitMetadata) {
        return create(objectNode.wrappedCdo().get(), globalId, commitMetadata, UPDATE);
    }

    private Object extractAndDehydrateEnumerable(Object propertyVal, EnumerableType propertyType, OwnerContext owner) {
        if (!propertyType.isFullyParametrized()){
            throw new JaversException(GENERIC_TYPE_NOT_PARAMETRIZED, propertyType.getBaseJavaType().toString());
        }

        EnumerableFunction dehydratorMapFunction;
        if (propertyType instanceof ContainerType) {
            JaversType itemType = typeMapper.getJaversType( ((ContainerType)propertyType).getItemClass() );
            dehydratorMapFunction = new DehydrateContainerFunction(itemType, globalIdFactory);
        }
        else if (propertyType instanceof MapType) {
            MapContentType mapContentType = typeMapper.getMapContentType((MapType) propertyType);
            dehydratorMapFunction = new DehydrateMapFunction(globalIdFactory, mapContentType);
        }
        else {
            throw new JaversException(NOT_IMPLEMENTED);
        }

        return  propertyType.map(propertyVal, dehydratorMapFunction, owner);
    }
}
