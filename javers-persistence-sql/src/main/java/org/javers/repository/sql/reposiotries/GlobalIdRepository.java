package org.javers.repository.sql.reposiotries;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.javers.common.collections.Optional;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;

import static org.javers.repository.sql.PolyUtil.queryForOptionalLong;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class GlobalIdRepository {

    private PolyJDBC polyJdbc;
    private JsonConverter jsonConverter;

    private Cache<GlobalId, Long> globalIdPkCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    public GlobalIdRepository(PolyJDBC javersPolyjdbc) {
        this.polyJdbc = javersPolyjdbc;
    }

    public long getOrInsertId(GlobalId globalId) {
        Optional<Long> pk = findGlobalIdPk(globalId);

        return pk.isPresent() ? pk.get() : insert(globalId);
    }

    public long getOrInsertClass(GlobalId globalId) {
        Class cdoClass = globalId.getManagedType().getBaseJavaClass();
        Optional<Long> lookup = findClassPk(cdoClass);

        return lookup.isPresent() ? lookup.get() : insertClass(cdoClass);
    }

    public Optional<Long> findClassPk(Class<?> cdoClass){
        SelectQuery query = polyJdbc.query()
            .select(CDO_CLASS_PK)
            .from(CDO_CLASS_TABLE_NAME)
            .where(CDO_CLASS_QUALIFIED_NAME + " = :qualifiedName ")
            .withArgument("qualifiedName", cdoClass.getName());

        return queryForOptionalLong(query, polyJdbc);
    }


    /**
     * cached
     */
    public Optional<Long> findGlobalIdPk(GlobalId globalId) {
        Long foundPk = globalIdPkCache.getIfPresent(globalId);

        if (foundPk != null){
            return Optional.of(foundPk);
        }

        Optional<Long> fresh = findGlobalIdPkRaw(globalId);
        if (fresh.isPresent()){
            globalIdPkCache.put(globalId, fresh.get());
        }

        return fresh;
    }

    private Optional<Long> findGlobalIdPkRaw(GlobalId globalId) {

        final String GLOBAL_ID_WITH_CDO_CLASS = GLOBAL_ID_TABLE_NAME + " g INNER JOIN " +
                     CDO_CLASS_TABLE_NAME + " c ON " + CDO_CLASS_PK + " = " + GLOBAL_ID_CLASS_FK;

        SelectQuery query = polyJdbc.query().select(GLOBAL_ID_PK);

        if (globalId instanceof ValueObjectId) {
            ValueObjectId valueObjectId  = (ValueObjectId) globalId;
            Optional<Long> ownerFk = findGlobalIdPk(valueObjectId.getOwnerId());
            if (ownerFk.isEmpty()){
                return Optional.empty();
            }
            query.from(GLOBAL_ID_TABLE_NAME)
                 .where(GLOBAL_ID_FRAGMENT + " = :fragment " +
                        "AND " + GLOBAL_ID_OWNER_ID_FK + " = :ownerFk ")
                 .withArgument("fragment", valueObjectId.getFragment())
                 .withArgument("ownerFk", ownerFk.get());
        }
        else if (globalId instanceof InstanceId){
            query.from(GLOBAL_ID_WITH_CDO_CLASS)
                .where("g." + GLOBAL_ID_LOCAL_ID + " = :localId " +
                       "AND c." + CDO_CLASS_QUALIFIED_NAME + " = :qualifiedName ")
                .withArgument("localId", jsonConverter.toJson(((InstanceId)globalId).getCdoId()))
                .withArgument("qualifiedName", globalId.getManagedType().getName());
        }
        else if (globalId instanceof UnboundedValueObjectId){
            query.from(GLOBAL_ID_WITH_CDO_CLASS)
                 .where("c." + CDO_CLASS_QUALIFIED_NAME + " = :qualifiedName ")
                 .withArgument("qualifiedName", globalId.getManagedType().getName());
        }

        return queryForOptionalLong(query, polyJdbc);
    }

    private long insert(GlobalId globalId) {
        long classPk = getOrInsertClass(globalId);

        InsertQuery query = polyJdbc.query()
                .insert()
                .into(GLOBAL_ID_TABLE_NAME);

        query.value(GLOBAL_ID_CLASS_FK, classPk);

        if (globalId instanceof ValueObjectId) {
            ValueObjectId valueObjectId  = (ValueObjectId) globalId;
            long ownerFk = getOrInsertId(valueObjectId.getOwnerId());
            query.value(GLOBAL_ID_FRAGMENT, valueObjectId.getFragment())
                 .value(GLOBAL_ID_OWNER_ID_FK, ownerFk);
        }
        else if (globalId instanceof InstanceId){
           query.value(GLOBAL_ID_LOCAL_ID, jsonConverter.toJson(((InstanceId)globalId).getCdoId()));
        }

        query.sequence(GLOBAL_ID_PK, GLOBAL_ID_PK_SEQ);

        long globalIdPk = polyJdbc.queryRunner().insert(query);
        return globalIdPk;
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
