package org.javers.repository.sql.repositories;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.*;
import org.javers.repository.sql.SqlRepositoryConfiguration;
import org.javers.repository.sql.schema.SchemaNameAware;
import org.javers.repository.sql.schema.TableNameProvider;
import org.javers.repository.sql.session.InsertBuilder;
import org.javers.repository.sql.session.SelectBuilder;
import org.javers.repository.sql.session.Session;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class GlobalIdRepository extends SchemaNameAware {

    private JsonConverter jsonConverter;
    private final boolean disableCache;

    private Cache<GlobalId, List<Long>> globalIdPkCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build();

    public GlobalIdRepository(TableNameProvider tableNameProvider, SqlRepositoryConfiguration configuration) {
        super(tableNameProvider);
        this.disableCache = configuration.isGlobalIdCacheDisabled();
    }

    public long getOrInsertId(GlobalId globalId, Optional<Boolean> isInitial, Session session) {
        if (isInitial.isPresent() && isInitial.get() == true) {
            return insert(globalId, session);
        }
        return findGlobalIdPk(globalId, session).stream().findFirst().orElseGet(() -> insert(globalId, session));
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
    public List<Long> findGlobalIdPk(GlobalId globalId, Session session) {
        if (disableCache){
            return findGlobalIdPkInDB(globalId, session);
        }

        List<Long> foundPks = globalIdPkCache.getIfPresent(globalId);

        if (foundPks != null){
            return foundPks;
        }

        List<Long> fresh = findGlobalIdPkInDB(globalId, session);
        if (!fresh.isEmpty()){
            globalIdPkCache.put(globalId, fresh);
        }

        return fresh;
    }

    private List<Long> findGlobalIdPkInDB(GlobalId globalId, Session session) {
        SelectBuilder select =  session.select(GLOBAL_ID_PK)
                .from(getGlobalIdTableNameWithSchema());

        if (globalId instanceof ValueObjectId) {
            final ValueObjectId valueObjectId  = (ValueObjectId) globalId;
            Optional<Long> ownerFk = findGlobalIdPk(valueObjectId.getOwnerId(), session).stream().findFirst();
            if (!ownerFk.isPresent()){
                return Collections.emptyList();
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

        return select.queryForListOfLong();
    }

    private void evictCacheFor(GlobalId globalId) {
        globalIdPkCache.invalidate(globalId);
    }

    private long insert(GlobalId globalId, Session session) {
        evictCacheFor(globalId);

        InsertBuilder insert = null;

        if (globalId instanceof ValueObjectId) {
            insert = session.insert("ValueObjectId");
            ValueObjectId valueObjectId  = (ValueObjectId) globalId;
            long ownerFk = getOrInsertId(valueObjectId.getOwnerId(), Optional.empty(), session);
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
              .sequence(GLOBAL_ID_PK, getGlobalIdPkSeqName().nameWithSchema())
              .executeAndGetSequence();
    }

    public void setJsonConverter(JsonConverter JSONConverter) {
        this.jsonConverter = JSONConverter;
    }
}
