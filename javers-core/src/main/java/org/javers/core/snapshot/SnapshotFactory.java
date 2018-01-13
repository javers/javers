package org.javers.core.snapshot;

import org.javers.common.collections.Defaults;
import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.JaversException;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.*;
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
        return cdoSnapshot()
                .withGlobalId(globalId)
                .withManagedType(managedType)
                .withCommitMetadata(commitMetadata)
                .withType(TERMINAL)
                .withVersion(previous.getVersion()+1)
                .build();
    }

    public CdoSnapshot createInitial(LiveCdo liveCdo, CommitMetadata commitMetadata) {
        return initSnapshotBuilder(liveCdo, commitMetadata)
                .withState(createSnapshotState(liveCdo))
                .withType(INITIAL)
                .markAllAsChanged()
                .withVersion(1L)
                .build();
    }

    public CdoSnapshot createUpdate(LiveCdo liveCdo, CdoSnapshot previous, CommitMetadata commitMetadata) {
        return initSnapshotBuilder(liveCdo, commitMetadata)
                .withState(createSnapshotState(liveCdo))
                .withType(UPDATE)
                .markChanged(previous)
                .withVersion(previous.getVersion()+1)
                .build();
    }

    public CdoSnapshotState createSnapshotState(LiveCdo liveCdo){
        CdoSnapshotStateBuilder stateBuilder = CdoSnapshotStateBuilder.cdoSnapshotState();
        for (JaversProperty property : liveCdo.getManagedType().getProperties()) {
            Object propertyVal = liveCdo.getPropertyValue(property.getName());
            if (Objects.equals(propertyVal, Defaults.defaultValue(property.getGenericType()))) {
                continue;
            }
            stateBuilder.withPropertyValue(property, dehydrateProperty(property, propertyVal, liveCdo.getGlobalId()));
        }
        return stateBuilder.build();
    }

    private Object extractAndDehydrateEnumerable(Object propertyVal, EnumerableType propertyType, OwnerContext owner) {
        EnumerableFunction dehydratorMapFunction;
        if (propertyType instanceof ContainerType) {
            JaversType itemType = typeMapper.getJaversType( ((ContainerType)propertyType).getItemClass() );
            dehydratorMapFunction = new DehydrateContainerFunction(itemType, globalIdFactory);
        }
        else if (propertyType instanceof KeyValueType) {
            MapContentType mapContentType = typeMapper.getMapContentType((KeyValueType) propertyType);
            dehydratorMapFunction = new DehydrateMapFunction(globalIdFactory, mapContentType);
        }
        else {
            throw new JaversException(NOT_IMPLEMENTED);
        }

        return  propertyType.map(propertyVal, dehydratorMapFunction, owner);
    }

    private CdoSnapshotBuilder initSnapshotBuilder(LiveCdo liveCdo, CommitMetadata commitMetadata) {
        return cdoSnapshot()
                .withGlobalId(liveCdo.getGlobalId())
                .withCommitMetadata(commitMetadata)
                .withManagedType(liveCdo.getManagedType());
    }

    private Object dehydrateProperty(JaversProperty property, Object propertyVal, GlobalId id){
        OwnerContext owner = new PropertyOwnerContext(id, property.getName());

        if (property.getType() instanceof EnumerableType) {
            return extractAndDehydrateEnumerable(propertyVal, (EnumerableType) property.getType(), owner);
        } else {
            return  globalIdFactory.dehydrate(propertyVal, property.getType(), owner);
        }
    }
}
