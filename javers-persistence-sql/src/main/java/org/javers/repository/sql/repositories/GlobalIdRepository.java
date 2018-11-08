package org.javers.repository.sql.repositories;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.repository.sql.SqlRepositoryConfiguration;
import org.javers.repository.sql.schema.SchemaNameAware;
import org.javers.repository.sql.schema.TableNameProvider;
import org.javers.repository.sql.session.InsertBuilder;
import org.javers.repository.sql.session.SelectBuilder;
import org.javers.repository.sql.session.Session;
import java.util.Optional;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class GlobalIdRepository extends SchemaNameAware {

    private JsonConverter jsonConverter;
    private final boolean disableCache;

    private Cache<GlobalId, Long> globalIdPkCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    public GlobalIdRepository(TableNameProvider tableNameProvider, SqlRepositoryConfiguration configuration) {
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
        SelectBuilder select =  session.select(GLOBAL_ID_PK)
                .from(getGlobalIdTableNameWithSchema());

        if (globalId instanceof ValueObjectId) {
            final ValueObjectId valueObjectId  = (ValueObjectId) globalId;
            Optional<Long> ownerFk = findGlobalIdPk(valueObjectId.getOwnerId(), session);
            if (!ownerFk.isPresent()){
                return Optional.empty();
            }
            select.and(GLOBAL_ID_FRAGMENT, valueObjectId.getFragment())
                  .and(GLOBAL_ID_OWNER_ID_FK, ownerFk.get())
                  .queryName("find PK of valueObjectId");
        }
        else if (globalId instanceof InstanceId){
            select.and(GLOBAL_ID_LOCAL_ID, jsonConverter.toJson(((InstanceId)globalId).getCdoId()))
                  .and(GLOBAL_ID_TYPE_NAME, globalId.getTypeName())
                  .queryName("find PK of InstanceId");
        }
        else if (globalId instanceof UnboundedValueObjectId){
            select.and(GLOBAL_ID_TYPE_NAME, globalId.getTypeName())
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
            insert.value(GLOBAL_ID_FRAGMENT, valueObjectId.getFragment())
                  .value(GLOBAL_ID_OWNER_ID_FK, ownerFk);
        }
        else if (globalId instanceof InstanceId) {
            insert = session.insert("InstanceId")
                    .value(GLOBAL_ID_TYPE_NAME, globalId.getTypeName())
                    .value(GLOBAL_ID_LOCAL_ID, jsonConverter.toJson(((InstanceId)globalId).getCdoId()));

        }
        else if (globalId instanceof UnboundedValueObjectId) {
            insert = session.insert("UnboundedValueObjectId")
                    .value(GLOBAL_ID_TYPE_NAME, globalId.getTypeName());
        }

        return insert.into(getGlobalIdTableNameWithSchema())
              .sequence(GLOBAL_ID_PK, getGlobalIdPkSeqWithSchema())
              .executeAndGetSequence();
    }

    public void setJsonConverter(JsonConverter JSONConverter) {
        this.jsonConverter = JSONConverter;
    }
}
