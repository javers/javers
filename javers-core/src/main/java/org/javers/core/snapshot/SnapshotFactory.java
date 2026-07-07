package org.javers.core.snapshot;

import org.javers.common.collections.Defaults;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.graph.Cdo;
import org.javers.core.graph.LiveNode;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.type.CustomComparableType;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.ListAsSetType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.SetType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.javers.core.metamodel.object.CdoSnapshotBuilder.cdoSnapshot;
import static org.javers.core.metamodel.object.SnapshotType.*;

/**
 * @author bartosz walacik
 */
public class SnapshotFactory {
    private final TypeMapper typeMapper;

    SnapshotFactory(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    public CdoSnapshot createTerminal(GlobalId globalId, CdoSnapshot previous, CommitMetadata commitMetadata) {
        ManagedType managedType = typeMapper.getJaversManagedType(globalId);
        return cdoSnapshot()
                .withGlobalId(globalId)
                .withManagedType(managedType)
                .withCommitMetadata(commitMetadata)
                .withType(TERMINAL)
                .withVersion(previous != null ? (previous.getVersion() + 1) : 1)
                .build();
    }

    CdoSnapshot createInitial(LiveNode liveNode, CommitMetadata commitMetadata) {
        return initSnapshotBuilder(liveNode, commitMetadata)
                .withState(createSnapshotState(liveNode))
                .withType(INITIAL)
                .markAllAsChanged()
                .withVersion(1L)
                .build();
    }

    CdoSnapshot createUpdate(LiveNode liveNode, CdoSnapshot previous, CommitMetadata commitMetadata) {
        return initSnapshotBuilder(liveNode, commitMetadata)
                .withState(createSnapshotState(liveNode))
                .withType(UPDATE)
                .markChanged(previous)
                .withVersion(previous.getVersion()+1)
                .build();
    }

    public CdoSnapshotState createSnapshotStateNoRefsSortedSets(Cdo liveCdo){
        return createSnapshotStateNoRefsSortedSets(liveCdo.getManagedType(), liveCdo.getWrappedCdo().get());
    }

    private CdoSnapshotState createSnapshotStateNoRefsSortedSets(ManagedType managedType, Object instance) {
        CdoSnapshotStateBuilder stateBuilder = CdoSnapshotStateBuilder.cdoSnapshotState();
        for (JaversProperty property : managedType.getProperties()) {
            if (property.getType() instanceof ManagedType ||
                typeMapper.isEnumerableOfManagedTypes(property.getType())) {
                continue;
            }

            Object propertyValue = property.get(instance);
            if (Objects.equals(propertyValue, Defaults.defaultValue(property.getGenericType()))) {
                continue;
            }

            if (property.getType() instanceof CustomComparableType) {
                String propertyValueToString = ((CustomComparableType) property.getType()).valueToString(propertyValue);
                stateBuilder.withPropertyValue(property, propertyValueToString);
                continue;
            }

            if (property.getType() instanceof SetType) {
                stateBuilder.withPropertyValue(property, toOrderedSet((Set<?>)propertyValue));
            }
            else if (property.getType() instanceof ListAsSetType) {
                stateBuilder.withPropertyValue(property, new TreeSet<>((List<?>)propertyValue));
            }
            else {
                stateBuilder.withPropertyValue(property, propertyValue);
            }
        }
        return stateBuilder.build();
    }

    private static <T> SortedSet<T> toOrderedSet(Set<T> set) {
        if (set instanceof SortedSet) {
            return (SortedSet)set;
        }
        return new TreeSet<>(set);
    }

    public CdoSnapshotState createSnapshotState(LiveNode liveNode){
        CdoSnapshotStateBuilder stateBuilder = CdoSnapshotStateBuilder.cdoSnapshotState();
        for (JaversProperty property : liveNode.getManagedType().getProperties()) {
            Object dehydratedPropertyValue = liveNode.getDehydratedPropertyValue(property);
            if (Objects.equals(dehydratedPropertyValue, Defaults.defaultValue(property.getGenericType()))) {
                continue;
            }
            if (stateBuilder.contains(property)) {
                throw new JaversException(JaversExceptionCode.SNAPSHOT_SERIALIZATION_ERROR, liveNode.getGlobalId().value(), property);
            }
            stateBuilder.withPropertyValue(property, dehydratedPropertyValue);
        }
        return stateBuilder.build();
    }

    private CdoSnapshotBuilder initSnapshotBuilder(LiveNode liveNode, CommitMetadata commitMetadata) {
        return cdoSnapshot()
                .withGlobalId(liveNode.getGlobalId())
                .withCommitMetadata(commitMetadata)
                .withManagedType(liveNode.getManagedType());
    }
}
