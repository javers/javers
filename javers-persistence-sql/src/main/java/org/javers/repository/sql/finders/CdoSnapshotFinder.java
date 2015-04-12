package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.clazz.ManagedClass;
import org.javers.core.metamodel.object.*;
import org.javers.repository.sql.reposiotries.GlobalIdRepository;
import org.javers.repository.sql.reposiotries.PersistentGlobalId;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.Order;
import org.polyjdbc.core.query.SelectQuery;

import java.util.Collections;
import java.util.List;

import static org.javers.repository.sql.PolyUtil.queryForOptionalLong;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CdoSnapshotFinder {

    private final PolyJDBC polyJDBC;
    private JsonConverter jsonConverter;
    private GlobalIdRepository globalIdRepository;

    public CdoSnapshotFinder(GlobalIdRepository globalIdRepository, PolyJDBC polyJDBC) {
        this.globalIdRepository = globalIdRepository;
        this.polyJDBC = polyJDBC;
    }

    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        PersistentGlobalId persistentGlobalId = globalIdRepository.findPersistedGlobalId(globalId);
        if (!persistentGlobalId.persisted()){
            return Optional.empty();
        }

        Optional<Long> maxSnapshot = selectMaxSnapshotPrimaryKey(persistentGlobalId);

        if (maxSnapshot.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(queryForCdoSnapshots(new SnapshotIdFilter(maxSnapshot.get(), persistentGlobalId), 1).get(0));
    }

    public List<CdoSnapshot> getStateHistory(ManagedClass givenClass, int limit) {
        Optional<Long> classPk = globalIdRepository.findClassPk(givenClass.getClientsClass());
        if (classPk.isEmpty()){
            return Collections.emptyList();
        }

        ClassIdFilter classIdFilter = new ClassIdFilter(classPk.get());

        return queryForCdoSnapshots(classIdFilter, limit);
    }

    public List<CdoSnapshot> getStateHistory(GlobalId globalId, int limit) {
        PersistentGlobalId persistentGlobalId = globalIdRepository.findPersistedGlobalId(globalId);
        if (!persistentGlobalId.persisted()){
            return Collections.emptyList();
        }

        return queryForCdoSnapshots(new GlobalIdFilter(persistentGlobalId), limit);
    }

    //TODO dependency injection
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    private List<CdoSnapshot> queryForCdoSnapshots(SnapshotFilter snapshotFilter, int limit){

        SelectQuery query =  polyJDBC.query()
            .select(snapshotFilter.select());
        snapshotFilter.addFrom(query);
        snapshotFilter.addWhere(query);
        query.orderBy(SNAPSHOT_PK, Order.DESC).limit(limit);

        GlobalId providedId = null;
        if (snapshotFilter instanceof GlobalIdFilter ) {
            providedId = ((GlobalIdFilter) snapshotFilter).globalId;
        } else if ( snapshotFilter instanceof SnapshotIdFilter ){
            providedId = ((SnapshotIdFilter) snapshotFilter).globalId;
        }

        return
        polyJDBC.queryRunner().queryList(query, new CdoSnapshotObjectMapper(jsonConverter, providedId));
    }

    private Optional<Long> selectMaxSnapshotPrimaryKey(PersistentGlobalId globalId) {
        SelectQuery query = polyJDBC.query()
            .select("MAX(" + SNAPSHOT_PK + ")")
            .from(SNAPSHOT_TABLE_NAME)
            .where(SNAPSHOT_GLOBAL_ID_FK + " = :globalIdPk")
            .withArgument("globalIdPk", globalId.getPrimaryKey());

        Optional<Long> result = queryForOptionalLong(query, polyJDBC);

        if (result.isPresent() && result.get() == 0){
            return Optional.empty();
        }
        return result;
    }

    private abstract class SnapshotFilter{
        static final String COMMIT_WITH_SNAPSHOT
                = SNAPSHOT_TABLE_NAME + " INNER JOIN " + COMMIT_TABLE_NAME + " ON " + COMMIT_PK + " = " + SNAPSHOT_COMMIT_FK;

        static final String BASE_FIELDS =
                SNAPSHOT_STATE + ", " +
                SNAPSHOT_TYPE + ", " +
                COMMIT_AUTHOR + ", " +
                COMMIT_COMMIT_DATE + ", " +
                COMMIT_COMMIT_ID;

        private final long primaryKey;
        private final String pkFieldName;

        public SnapshotFilter(long primaryKey, String pkFieldName) {
            this.primaryKey = primaryKey;
            this.pkFieldName = pkFieldName;
        }

        void addWhere(SelectQuery query) {
            String argName = this.getClass().getSimpleName()+"Pk";
            query.where(pkFieldName + " = :"+argName).withArgument(argName, primaryKey);
        }

        void addFrom(SelectQuery query) {
            query.from(COMMIT_WITH_SNAPSHOT);
        }

        String select(){
            return BASE_FIELDS;
        }
    }

    private class ClassIdFilter extends SnapshotFilter{
        ClassIdFilter(long classPk) {
            super(classPk, "g."+GLOBAL_ID_CLASS_FK);
        }

        @Override
        void addFrom(SelectQuery query) {
            final String JOIN_GLOBAL_ID_TO_SNAPSHOT
                    = " INNER JOIN " + GLOBAL_ID_TABLE_NAME + " as g ON g." + GLOBAL_ID_PK + " = " + SNAPSHOT_GLOBAL_ID_FK +
                    " INNER JOIN " + CDO_CLASS_TABLE_NAME + " as g_c ON g_c." + CDO_CLASS_PK + " = g."+GLOBAL_ID_CLASS_FK +
                    " LEFT OUTER JOIN " + GLOBAL_ID_TABLE_NAME + " as o ON o." + GLOBAL_ID_PK + " = g." + GLOBAL_ID_OWNER_ID_FK +
                    " LEFT OUTER JOIN " + CDO_CLASS_TABLE_NAME + " as o_c ON o_c." + CDO_CLASS_PK + " = o."+GLOBAL_ID_CLASS_FK;

            query.from(COMMIT_WITH_SNAPSHOT + JOIN_GLOBAL_ID_TO_SNAPSHOT);
        }

        @Override
        String select() {
            return BASE_FIELDS + ", " +
                   "g."+GLOBAL_ID_LOCAL_ID + ", " +
                   "g."+GLOBAL_ID_FRAGMENT + ", " +
                   "g."+GLOBAL_ID_OWNER_ID_FK + ", " +
                   "g_c."+CDO_CLASS_QUALIFIED_NAME + ", " +
                   "o."+GLOBAL_ID_LOCAL_ID + " as owner_" + GLOBAL_ID_LOCAL_ID + ", " +
                   "o."+GLOBAL_ID_FRAGMENT + " as owner_" + GLOBAL_ID_FRAGMENT + ", " +
                   "o_c."+CDO_CLASS_QUALIFIED_NAME + " as owner_" + CDO_CLASS_QUALIFIED_NAME;
        }
    }

    private class SnapshotIdFilter extends SnapshotFilter{
        private final GlobalId globalId;

        SnapshotIdFilter(long snapshotId, GlobalId globalId) {
            super(snapshotId, SNAPSHOT_PK);
            this.globalId = globalId;
        }
    }

    private class GlobalIdFilter extends SnapshotFilter{
        private final GlobalId globalId;

        GlobalIdFilter(PersistentGlobalId id) {
           super(id.getPrimaryKey(), SNAPSHOT_GLOBAL_ID_FK);
           this.globalId = id.getInstance();
        }
    }

}