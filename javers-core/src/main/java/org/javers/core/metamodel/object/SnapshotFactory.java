package org.javers.core.metamodel.object;

import org.javers.common.collections.Defaults;
import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Objects;
import org.javers.common.exception.JaversException;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;
import static org.javers.common.exception.JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED;
import static org.javers.common.exception.JaversExceptionCode.NOT_IMPLEMENTED;
import static org.javers.core.metamodel.object.CdoSnapshotBuilder.cdoSnapshot;

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

    CdoSnapshot create(Object liveCdo, GlobalId id, CommitMetadata commitMetadata) {
        return create(liveCdo, id, commitMetadata, SnapshotType.UPDATE);
    }

    /**
     * @throws JaversException GENERIC_TYPE_NOT_PARAMETRIZED
     */
    CdoSnapshot create(Object liveCdo, GlobalId id, CommitMetadata commitMetadata, SnapshotType type) {
        CdoSnapshotBuilder snapshot =
                cdoSnapshot(id, commitMetadata).withType(type);

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

            snapshot.withPropertyValue(property, filteredPropertyVal);
        }

        return snapshot.build();
    }

    public CdoSnapshot create(ObjectNode objectNode, CommitMetadata commitMetadata, SnapshotType type) {
        return create(objectNode.wrappedCdo().get(), objectNode.getGlobalId(), commitMetadata, type);
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
            dehydratorMapFunction = new DehydrateMapFunction((MapType) propertyType, typeMapper, globalIdFactory);
        }
        else {
            throw new JaversException(NOT_IMPLEMENTED);
        }

        return  propertyType.map(propertyVal, dehydratorMapFunction, owner);
    }
}
