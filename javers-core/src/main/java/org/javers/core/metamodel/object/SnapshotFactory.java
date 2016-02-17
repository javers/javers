package org.javers.core.metamodel.object;

import org.javers.common.collections.Defaults;
import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.JaversException;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

import java.util.Objects;

import static org.javers.common.exception.JaversExceptionCode.GENERIC_TYPE_NOT_PARAMETRIZED;
import static org.javers.common.exception.JaversExceptionCode.NOT_IMPLEMENTED;
import static org.javers.core.metamodel.object.CdoSnapshotBuilder.cdoSnapshot;
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

    public CdoSnapshot createTerminal(GlobalId globalId, CommitMetadata commitMetadata) {
        ManagedType managedType = typeMapper.getJaversManagedType(globalId);
        return cdoSnapshot(globalId, commitMetadata, managedType).withType(TERMINAL).build();
    }

    public CdoSnapshot createInitial(CdoWrapper cdoWrapper, CommitMetadata commitMetadata) {
        return createSnapshotState(cdoWrapper, commitMetadata)
                .withType(INITIAL)
                .markAllAsChanged()
                .withVersion(1L)
                .build();
    }

    public CdoSnapshot createUpdate(CdoWrapper cdoWrapper, CdoSnapshot previous, CommitMetadata commitMetadata) {
        return createSnapshotState(cdoWrapper, commitMetadata)
                .withType(UPDATE)
                .markChanged(previous)
                .withVersion(previous.getVersion()+1)
                .build();
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

    private CdoSnapshotBuilder createSnapshotState(CdoWrapper cdoWrapper, CommitMetadata commitMetadata){
        CdoSnapshotBuilder snapshotBuilder = cdoSnapshot(cdoWrapper.getGlobalId(), commitMetadata, cdoWrapper.getManagedType());

        for (Property property : cdoWrapper.getManagedType().getProperties()) {
            Object propertyVal = cdoWrapper.getPropertyValue(property.getName());
            if (Objects.equals(propertyVal, Defaults.defaultValue(property.getGenericType()))) {
                continue;
            }
            snapshotBuilder.withPropertyValue(property, dehydrateProperty(property, propertyVal, cdoWrapper.getGlobalId()));
        }
        return snapshotBuilder;
    }

    private Object dehydrateProperty(Property property, Object propertyVal, GlobalId id){
        JaversType propertyType = typeMapper.getPropertyType(property);
        OwnerContext owner = new OwnerContext(id, property.getName());

        if (propertyType instanceof EnumerableType) {
            return extractAndDehydrateEnumerable(propertyVal, (EnumerableType) propertyType, owner);
        } else {
            return  globalIdFactory.dehydrate(propertyVal, propertyType, owner);
        }
    }
}
