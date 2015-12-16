package org.javers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.javers.common.collections.Optional;
import org.javers.common.reflection.JaversField;
import org.javers.core.Javers;
import org.javers.core.commit.CommitId;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.repository.jql.InstanceIdDTO;
import org.javers.core.metamodel.property.Property;
import org.javers.repository.jql.QueryBuilder;

/**
 * Loads under certain assumptions the original entity state for given
 * CdoSnapshot or CommitId.<br>
 * 1) Loads the whole object graph eagerly (lazy loading would require the use
 * of proxy objects). <br>
 * 2) All entities have constructor with no arguments -> Java Beans standard. <br>
 * 3) Each entity field ({@link JaversField}) has a setter method with the
 * argument of the same type as field -> Java Beans standard. <br>
 * 4) Since Javers lacks the function for retrieving the CdoSnapshot based on
 * nearest commitId -> all CdoSnapshots have to be loaded (assumes that limit =
 * -1 means all snapshots, see implementation of
 * {@linkplain #compileEntityStateForCommitId(InstanceId, String)}). <br>
 * 5) Maps and other types are not yet supported. <br>
 */
public class JaversSnapshotsCompiler {

    private static final double EPS = 0.001;
    private Javers javers;

    private Map<Object, Object> idToEntityMap = new HashMap<>();

    public JaversSnapshotsCompiler(Javers javers) {
        this.javers = javers;
    }

    public void clearCache() {
        idToEntityMap.clear();
    }

    /**
     * Loads original entity state for given CdoSnapshot
     *
     * @param snapshot
     * @return
     */
    public Object compileEntityStateFromSnapshot(CdoSnapshot snapshot) {
        clearCache();
        try {
            return compileEntityInternal(snapshot);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads original entity state for given instanceId and commitId
     *
     * @param instanceId
     * @param commitId
     * @return
     */
    public Object compileEntityStateForCommitId(InstanceId instanceId,
                                                String commitId) {
        clearCache();
        try {
            return compileEntityForCommitId(instanceId, CommitId.valueOf(commitId));
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads the latest commited entity state for given instanceId
     *
     * @param instanceId
     * @return
     */
    public Object compileLatestEntityState(InstanceId instanceId) {
        InstanceIdDTO dto = convertInstanceIdToDTO(instanceId);
        Optional<CdoSnapshot> latestSnapshot = javers.getLatestSnapshot(dto, dto.getClass());
        //Optional<CdoSnapshot> latestSnapshot = javers.getLatestSnapshot()getLatestSnapshot(convertInstanceIdToDTO(instanceId));
        return latestSnapshot.isPresent() ? compileEntityStateFromSnapshot(latestSnapshot.get()) : null;
    }

    /**
     * Load latest entity state for entity
     *
     * @param entity
     * @return
     */
    public Object compileLatestEntityStateForEntity(Object entity) {
        return compileLatestEntityState(javers.idBuilder().instanceId(entity));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object compileEntityInternal(CdoSnapshot snap) throws IllegalAccessException, InstantiationException {

        if (idToEntityMap.containsKey(((InstanceId) snap.getGlobalId()).getCdoId()))
            return idToEntityMap.get(((InstanceId)snap.getGlobalId()).getCdoId());

        Class clientsClass = snap.getManagedType().getBaseJavaClass();

        Object instance = clientsClass.newInstance();
        idToEntityMap.put(((InstanceId)snap.getGlobalId()).getCdoId(), instance);
        //for (Property property : snap.getState().getProperties()) {
        for (String propertyName : snap.getState().getProperties()) {
            Object propertyValue = snap.getPropertyValue(propertyName);
            Object propertyValueToSet = propertyValue;
            if (propertyValue != null) {
                try {
                    Method setter = getSetterForProperty(clientsClass, propertyName);
                    Class<?> targetType = setter.getParameterTypes()[0];
                    if (targetType.isAssignableFrom(java.util.List.class)
                            || targetType.isAssignableFrom(java.util.Set.class)) {
                        Collection collection = (Collection) propertyValue;
                        Set toBeDeleted = new HashSet<>();
                        for (Object element : collection.toArray()){
                            if (element instanceof InstanceId) {
                                InstanceId instanceId = (InstanceId) element;

                                Object targetObject = compileEntityForCommitId(
                                        instanceId, snap.getCommitId());
                                collection.add(targetObject);
                                toBeDeleted.add(element);
                            }
                        }
                        for (Object element : toBeDeleted) {
                            collection.remove(element);
                        }
                    } else if (propertyValue instanceof InstanceId) {
                        propertyValueToSet = compileEntityForCommitId(
                                (InstanceId) propertyValue, snap.getCommitId());
                    }
                    setter.invoke(instance, propertyValueToSet);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        }

        return instance;
    }

    /**
     * Programmatic way is chosen to find corresponding CdoSnapshot, however
     * better practice would be to have the repository method for searching
     *
     * @param instanceId
     * @param commitId
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Object compileEntityForCommitId(InstanceId instanceId, CommitId commitId) throws IllegalAccessException,
            InstantiationException {

        // TODO here the Javers function for loading the CdoSnapshot for the
        // given commitId should be called, but the current Javers
        // implementation doesn't provide it
        // TODO assumes that limit = -1 means "all CdoSnapshots"
        //List<CdoSnapshot> snapshots = javers.getStateHistory(convertInstanceIdToDTO(instanceId), -1); // load all
        //List<CdoSnapshot> snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(instanceId, instanceId.getgetBaseJavaClass() ).build());
        List<CdoSnapshot> snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(instanceId, instanceId.getClass()).build());

        double commitToBeSearched = commitIdAsDouble(commitId);
        CdoSnapshot snapFound = null;
        // since all snapshot are sorted from the highest commit value to the
        // lowest one
        for (CdoSnapshot snap : snapshots) {
            if (commitIdAsDouble(snap.getCommitId()) < (commitToBeSearched + EPS)) {
                snapFound = snap;
            }
        }

        Object instance = compileEntityInternal(snapFound);

        return instance;
    }

    private InstanceIdDTO convertInstanceIdToDTO(InstanceId instanceId) {
        //return InstanceIdDTO.instanceId(instanceId.getCdoId(), instanceId.getManagedType().getBaseJavaClass());//..getClientsClass());
        return InstanceIdDTO.instanceId(instanceId.getCdoId(), instanceId.getClass());//..getClientsClass());
    }

    private double commitIdAsDouble(CommitId commitId) {
        return Double.parseDouble(commitId.value());
    }

    @SuppressWarnings("rawtypes")
    private Method getSetterForProperty(Class clientsClass, String propertyName)
            throws NoSuchFieldException, NoSuchMethodException {
        Field declaredField = clientsClass.getDeclaredField(propertyName);
        @SuppressWarnings("unchecked")
        Method setter = clientsClass.getDeclaredMethod(
                getSetterNameForProperty(propertyName), declaredField.getType());
        return setter;
    }

    private String getSetterNameForProperty(String propertyName) {
        return "set" + propertyName.substring(0, 1).toUpperCase()
                + propertyName.substring(1);
    }

}