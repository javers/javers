package org.javers.repository.sql.repositories;

import java.util.Optional;

import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.repository.sql.SqlRepositoryConfiguration;
import org.javers.repository.sql.schema.DBNameProvider;
import org.javers.repository.sql.schema.SchemaNameAware;
import org.javers.repository.sql.session.InsertBuilder;
import org.javers.repository.sql.session.SelectBuilder;
import org.javers.repository.sql.session.Session;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class GlobalIdRepository extends SchemaNameAware {

    private JsonConverter jsonConverter;
    private final boolean disableCache;

    private Cache<GlobalId, Long> globalIdPkCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    public GlobalIdRepository(DBNameProvider tableNameProvider, SqlRepositoryConfiguration configuration) {
        super(tableNameProvider);
        this.disableCache = configuration.isGlobalIdCacheDisabled();
    }

    public long getOrInsertId(GlobalId globalId, Session session) {
        Optional<Long> pk = findGlobalIdPk(globalId, session);
        return pk.isPresent() ? pk.get() : insert(globalId, session);
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
    public Optional<Long> findGlobalIdPk(GlobalId globalId, Session session) {
        if (disableCache){
            return findGlobalIdPkInDB(globalId, session);
        }

        Long foundPk = globalIdPkCache.getIfPresent(globalId);

        if (foundPk != null){
            return Optional.of(foundPk);
        }

        Optional<Long> fresh = findGlobalIdPkInDB(globalId, session);
        if (fresh.isPresent()){
            globalIdPkCache.put(globalId, fresh.get());
        }

        return fresh;
    }

    private Optional<Long> findGlobalIdPkInDB(GlobalId globalId, Session session) {
        SelectBuilder select =  session.select(getGlobalIdPKColunmName())
                .from(getGlobalIdTableNameWithSchema());

        if (globalId instanceof ValueObjectId) {
            final ValueObjectId valueObjectId  = (ValueObjectId) globalId;
            Optional<Long> ownerFk = findGlobalIdPk(valueObjectId.getOwnerId(), session);
            if (!ownerFk.isPresent()){
                return Optional.empty();
            }
            select.and(getGlobalIdFragmentColumnName(), valueObjectId.getFragment())
                  .and(getGlobalIdOwnerIDFKColumnName(), ownerFk.get())
                  .queryName("find PK of valueObjectId");
        }
        else if (globalId instanceof InstanceId){
            select.and(getGlobalIdLocalIdColumnName(), jsonConverter.toJson(((InstanceId)globalId).getCdoId()))
                  .and(getGlobalIdTypeNameColumnName(), globalId.getTypeName())
                  .queryName("find PK of InstanceId");
        }
        else if (globalId instanceof UnboundedValueObjectId){
            select.and(getGlobalIdTypeNameColumnName(), globalId.getTypeName())
                  .queryName("find PK of UnboundedValueObjectId");
        }

        return select.queryForOptionalLong();
    }

    private long insert(GlobalId globalId, Session session) {
        InsertBuilder insert = null;

        if (globalId instanceof ValueObjectId) {
            insert = session.insert("ValueObjectId");
            ValueObjectId valueObjectId  = (ValueObjectId) globalId;
            long ownerFk = getOrInsertId(valueObjectId.getOwnerId(), session);
            insert.value(getGlobalIdFragmentColumnName(), valueObjectId.getFragment())
                  .value(getGlobalIdOwnerIDFKColumnName(), ownerFk);
        }
        else if (globalId instanceof InstanceId) {
            insert = session.insert("InstanceId")
                    .value(getGlobalIdTypeNameColumnName(), globalId.getTypeName())
                    .value(getGlobalIdLocalIdColumnName(), jsonConverter.toJson(((InstanceId)globalId).getCdoId()));

        }
        else if (globalId instanceof UnboundedValueObjectId) {
            insert = session.insert("UnboundedValueObjectId")
                    .value(getGlobalIdTypeNameColumnName(), globalId.getTypeName());
        }

        return insert.into(getGlobalIdTableNameWithSchema())
              .sequence(getGlobalIdPKColunmName(), getGlobalIdPkSeqName().nameWithSchema())
              .executeAndGetSequence();
    }

    public void setJsonConverter(JsonConverter JSONConverter) {
        this.jsonConverter = JSONConverter;
    }
}
