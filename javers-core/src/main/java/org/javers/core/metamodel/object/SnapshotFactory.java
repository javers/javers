package org.javers.core.metamodel.object;

import org.javers.common.collections.Defaults;
import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Objects;
import org.javers.common.exception.JaversException;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

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
        return cdoSnapshot(globalId, commitMetadata).withType(TERMINAL).build();
    }

    public CdoSnapshot createInitial(Object liveCdo, GlobalId globalId, CommitMetadata commitMetadata) {
        return createSnapshotState(liveCdo, globalId, commitMetadata)
                .withType(INITIAL)
                .markAllAsChanged()
                .build();
    }

    public CdoSnapshot createUpdate(Object liveCdo, CdoSnapshot previous, CommitMetadata commitMetadata) {
        return createSnapshotState(liveCdo, previous.getGlobalId(), commitMetadata)
                .withType(UPDATE)
                .markChanged(previous)
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

    private CdoSnapshotBuilder createSnapshotState(Object liveCdo, GlobalId id, CommitMetadata commitMetadata){
        CdoSnapshotBuilder snapshotBuilder = cdoSnapshot(id, commitMetadata);

        for (Property property : id.getCdoClass().getProperties()) {
            Object propertyVal = property.get(liveCdo);
            if (Objects.nullSafeEquals(propertyVal, Defaults.defaultValue(property.getType()))) {
                continue;
            }
            snapshotBuilder.withPropertyValue(property, dehydrateProperty(property, propertyVal, id));
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
