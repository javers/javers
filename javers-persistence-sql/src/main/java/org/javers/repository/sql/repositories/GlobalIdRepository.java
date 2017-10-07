package org.javers.repository.sql.repositories;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Optional;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.repository.sql.SqlRepositoryConfiguration;
import org.javers.repository.sql.schema.SchemaNameAware;
import org.javers.repository.sql.schema.TableNameProvider;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;

import static org.javers.repository.sql.poly.PolyUtil.queryForOptionalLong;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class GlobalIdRepository extends SchemaNameAware {

    private final PolyJDBC polyJdbc;
    private JsonConverter jsonConverter;
    private final boolean disableCache;

    private Cache<GlobalId, Long> globalIdPkCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    public GlobalIdRepository(PolyJDBC javersPolyjdbc, TableNameProvider tableNameProvider, SqlRepositoryConfiguration configuration) {
        super(tableNameProvider);
        this.polyJdbc = javersPolyjdbc;
        this.disableCache = configuration.isGlobalIdCacheDisabled();
    }

    public long getOrInsertId(GlobalId globalId) {
        Optional<Long> pk = findGlobalIdPk(globalId);
        return pk.isPresent() ? pk.get() : insert(globalId);
    }

    public void evictCache() {
        globalIdPkCache.invalidateAll();
    }

    public int getGlobalIdPkCacheSize() {
        return (int)globalIdPkCache.size();
    }

    /**
     * cached
     */
    public Optional<Long> findGlobalIdPk(GlobalId globalId) {
        if (disableCache){
            return findGlobalIdPkInDB(globalId);
        }

        Long foundPk = globalIdPkCache.getIfPresent(globalId);

        if (foundPk != null){
            return Optional.of(foundPk);
        }

        Optional<Long> fresh = findGlobalIdPkInDB(globalId);
        if (fresh.isPresent()){
            globalIdPkCache.put(globalId, fresh.get());
        }

        return fresh;
    }

    private Optional<Long> findGlobalIdPkInDB(GlobalId globalId) {

        SelectQuery query = polyJdbc.query().select(GLOBAL_ID_PK).from(getGlobalIdTableNameWithSchema());

        if (globalId instanceof ValueObjectId) {
            final ValueObjectId valueObjectId  = (ValueObjectId) globalId;
            Optional<Long> ownerFk = findGlobalIdPk(valueObjectId.getOwnerId());
            if (!ownerFk.isPresent()){
                return Optional.empty();
            }
            query.where(GLOBAL_ID_FRAGMENT + " = :fragment ")
                 .withArgument("fragment", valueObjectId.getFragment())
                 .append("AND " + GLOBAL_ID_OWNER_ID_FK + " = :ownerFk ")
                 .withArgument("ownerFk", ownerFk.get());
        }
        else if (globalId instanceof InstanceId){
            query
                .where(GLOBAL_ID_LOCAL_ID + " = :localId ")
                .withArgument("localId", jsonConverter.toJson(((InstanceId)globalId).getCdoId()))
                .append("AND " + GLOBAL_ID_TYPE_NAME + " = :qualifiedName ")
                .withArgument("qualifiedName", globalId.getTypeName());
        }
        else if (globalId instanceof UnboundedValueObjectId){
            query
                .where(GLOBAL_ID_TYPE_NAME + " = :qualifiedName ")
                .withArgument("qualifiedName", globalId.getTypeName());
        }

        return queryForOptionalLong(query, polyJdbc);
    }

    private long insert(GlobalId globalId) {
        InsertQuery query = polyJdbc.query()
            .insert()
            .into(getGlobalIdTableNameWithSchema());


        if (globalId instanceof ValueObjectId) {
            ValueObjectId valueObjectId  = (ValueObjectId) globalId;
            long ownerFk = getOrInsertId(valueObjectId.getOwnerId());
            query.value(GLOBAL_ID_FRAGMENT, valueObjectId.getFragment())
                 .value(GLOBAL_ID_OWNER_ID_FK, ownerFk);
        }
        else if (globalId instanceof InstanceId) {
           query.value(GLOBAL_ID_TYPE_NAME, globalId.getTypeName())
                .value(GLOBAL_ID_LOCAL_ID, jsonConverter.toJson(((InstanceId)globalId).getCdoId()));

        }
        else if (globalId instanceof UnboundedValueObjectId) {
            query.value(GLOBAL_ID_TYPE_NAME, globalId.getTypeName());
        }

        query.sequence(GLOBAL_ID_PK, getGlobalIdPkSeqWithSchema());

        return polyJdbc.queryRunner().insert(query);
    }

    public void setJsonConverter(JsonConverter JSONConverter) {
        this.jsonConverter = JSONConverter;
    }
}
