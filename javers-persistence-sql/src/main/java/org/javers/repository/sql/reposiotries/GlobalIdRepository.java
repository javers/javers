package org.javers.repository.sql.reposiotries;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.GlobalId;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;

import static org.javers.common.validation.Validate.conditionFulfilled;
import static org.javers.repository.sql.PolyUtil.queryForOptionalLong;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class GlobalIdRepository {

    private PolyJDBC polyJdbc;
    private JsonConverter jsonConverter;

    public GlobalIdRepository(PolyJDBC javersPolyjdbc) {
        this.polyJdbc = javersPolyjdbc;
    }

    public PersistentGlobalId findPersistedGlobalId(GlobalId globalId) {
        if (globalId instanceof PersistentGlobalId) {
            PersistentGlobalId persistentGlobalId = (PersistentGlobalId) globalId;
            conditionFulfilled(persistentGlobalId.persisted(), "unexpected empty persistentGlobalId: " + globalId.value());
            //already persisted
            return persistentGlobalId;
        }

        Optional<Long> globalIdPrimaryKey = findGlobalIdPk(globalId);
        return new PersistentGlobalId(globalId, globalIdPrimaryKey);
    }

    public long getOrInsertId(GlobalId globalId) {
        PersistentGlobalId lookup = findPersistedGlobalId(globalId);

        return lookup.persisted() ? lookup.getPrimaryKey() : insert(globalId);
    }

    public long getOrInsertClass(GlobalId globalId) {
        Class cdoClass = globalId.getCdoClass().getClientsClass();
        Optional<Long> lookup = findClassPk(cdoClass);

        return lookup.isPresent() ? lookup.get() : insertClass(cdoClass);
    }

    private Optional<Long> findGlobalIdPk(GlobalId globalId){
        SelectQuery query = polyJdbc.query()
                .select(GLOBAL_ID_PK)
                .from(  GLOBAL_ID_TABLE_NAME + " as g INNER JOIN " +
                        CDO_CLASS_TABLE_NAME + " as c ON " + CDO_CLASS_PK + " = " + GLOBAL_ID_CLASS_FK)
                .where("g." + GLOBAL_ID_LOCAL_ID + " = :localId " +
                        "AND c." + CDO_CLASS_QUALIFIED_NAME + " = :qualifiedName ")
                .withArgument("localId", jsonConverter.toJson(globalId.getCdoId()))
                .withArgument("qualifiedName", globalId.getCdoClass().getName());

        return queryForOptionalLong(query, polyJdbc);
    }

    private long insert(GlobalId globalId) {

        long classPk = getOrInsertClass(globalId);

        InsertQuery insertGlobalIdQuery = polyJdbc.query()
                .insert()
                .into(GLOBAL_ID_TABLE_NAME)
                .value(GLOBAL_ID_LOCAL_ID, jsonConverter.toJson(globalId.getCdoId()))
                .value(GLOBAL_ID_CLASS_FK, classPk)
                .sequence(GLOBAL_ID_PK, GLOBAL_ID_PK_SEQ);

        long globalIdPk = polyJdbc.queryRunner().insert(insertGlobalIdQuery);

        return globalIdPk;
    }

    private Optional<Long> findClassPk(Class<?> cdoClass){
        SelectQuery query = polyJdbc.query()
                .select(CDO_CLASS_PK)
                .from(CDO_CLASS_TABLE_NAME)
                .where(CDO_CLASS_QUALIFIED_NAME + " = :qualifiedName ")
                .withArgument("qualifiedName", cdoClass.getName());

        return queryForOptionalLong(query, polyJdbc);
    }

    private long insertClass(Class<?> cdoClass){
        InsertQuery query = polyJdbc.query()
                .insert()
                .into(CDO_CLASS_TABLE_NAME)
                .value(CDO_CLASS_QUALIFIED_NAME, cdoClass.getName())
                .sequence(CDO_CLASS_PK, CDO_PK_SEQ_NAME);
        return polyJdbc.queryRunner().insert(query);
    }

    //TODO dependency injection
    public void setJsonConverter(JsonConverter JSONConverter) {
        this.jsonConverter = JSONConverter;
    }

}
