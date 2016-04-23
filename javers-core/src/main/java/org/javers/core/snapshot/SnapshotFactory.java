package org.javers.core.snapshot;

import org.javers.common.collections.Defaults;
import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.JaversException;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

import java.util.Objects;

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

    public CdoSnapshot createTerminal(GlobalId globalId, CdoSnapshot previous, CommitMetadata commitMetadata) {
        ManagedType managedType = typeMapper.getJaversManagedType(globalId);
        return cdoSnapshot(globalId, commitMetadata, managedType)
                .withType(TERMINAL)
                .withVersion(previous.getVersion()+1)
                .build();
    }

    public CdoSnapshot createInitial(CdoWrapper cdoWrapper, CommitMetadata commitMetadata) {
        return initSnapshotBuilder(cdoWrapper, commitMetadata)
                .withState(createSnapshotState(cdoWrapper))
                .withType(INITIAL)
                .markAllAsChanged()
                .withVersion(1L)
                .build();
    }

    public CdoSnapshot createUpdate(CdoWrapper cdoWrapper, CdoSnapshot previous, CommitMetadata commitMetadata) {
        return initSnapshotBuilder(cdoWrapper, commitMetadata)
                .withState(createSnapshotState(cdoWrapper))
                .withType(UPDATE)
                .markChanged(previous)
                .withVersion(previous.getVersion()+1)
                .build();
    }

    public CdoSnapshotState createSnapshotState(CdoWrapper cdoWrapper){
        CdoSnapshotStateBuilder stateBuilder = CdoSnapshotStateBuilder.cdoSnapshotState();
        for (Property property : cdoWrapper.getManagedType().getProperties()) {
            Object propertyVal = cdoWrapper.getPropertyValue(property.getName());
            if (Objects.equals(propertyVal, Defaults.defaultValue(property.getGenericType()))) {
                continue;
            }
            stateBuilder.withPropertyValue(property, dehydrateProperty(property, propertyVal, cdoWrapper.getGlobalId()));
        }
        return stateBuilder.build();
    }

    private Object extractAndDehydrateEnumerable(Object propertyVal, EnumerableType propertyType, OwnerContext owner) {
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

    private CdoSnapshotBuilder initSnapshotBuilder(CdoWrapper cdoWrapper, CommitMetadata commitMetadata){
        return cdoSnapshot(cdoWrapper.getGlobalId(), commitMetadata, cdoWrapper.getManagedType());
    }

    private Object dehydrateProperty(Property property, Object propertyVal, GlobalId id){
        JaversType propertyType = typeMapper.getPropertyType(property);
        OwnerContext owner = new PropertyOwnerContext(id, property.getName());

        if (propertyType instanceof EnumerableType) {
            return extractAndDehydrateEnumerable(propertyVal, (EnumerableType) propertyType, owner);
        } else {
            return  globalIdFactory.dehydrate(propertyVal, propertyType, owner);
        }
    }
}
