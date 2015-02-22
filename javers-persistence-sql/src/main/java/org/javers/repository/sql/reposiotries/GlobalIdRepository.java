package org.javers.repository.sql.reposiotries;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.GlobalId;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.javers.repository.sql.PolyUtil.queryForOptionalLong;
import static org.javers.repository.sql.schema.FixedSchemaFactory.*;
import static org.slf4j.LoggerFactory.getLogger;

public class GlobalIdRepository {
    private static final Logger logger = getLogger(GlobalIdRepository.class);

    private PolyJDBC polyJdbc;
    private JsonConverter jsonConverter;
    private GlobalIdCache cache = new GlobalIdCache(10_000, 10);

    private Map<String, Optional<Long>> dummyIdCache = new HashMap<>();

    public GlobalIdRepository(PolyJDBC javersPolyjdbc) {
        this.polyJdbc = javersPolyjdbc;
    }

    public long getOrInsertId(GlobalId globalId) {
        PersistentGlobalId lookup = findPersistedGlobalId(globalId);

        return lookup.found() ? lookup.getPrimaryKey() : insert(globalId);
    }

    public long getOrInsertClass(GlobalId globalId) {
        Class cdoClass = globalId.getCdoClass().getClientsClass();
        Optional<Long> lookup = findClassPk(cdoClass);

        return lookup.isPresent() ? lookup.get() : insertClass(cdoClass);
    }

    public PersistentGlobalId findPersistedGlobalId(GlobalId globalId){
        //cached
        Optional<Long> globalIdPrimaryKey = cache.load(globalId);

        return new PersistentGlobalId(globalId, globalIdPrimaryKey);
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

        //cache write
        cache.explicitPut(globalId, globalIdPk);

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

    private class GlobalIdCache {
        private LoadingCache<GlobalId, Optional<Long>> primaryKeys;

        GlobalIdCache(long maxSize, long timeToLiveSeconds) {
            logger.info("initializing cache for GlobalId primaryKeys, maxSize: "+maxSize + ", timeToLive: " +timeToLiveSeconds+"s");

            primaryKeys = CacheBuilder.newBuilder()
                    .maximumSize(maxSize)
                    .expireAfterWrite(timeToLiveSeconds, TimeUnit.SECONDS)
                    .build(
                            new CacheLoader<GlobalId, Optional<Long>>() {
                                @Override
                                public Optional<Long> load(GlobalId key) throws Exception {
                                    return findGlobalIdPk(key);
                                }
                            });
        }

        void explicitPut(GlobalId globalId, long primaryKey) {
            primaryKeys.put(globalId, Optional.of(primaryKey));
        }

        Optional<Long> load(GlobalId globalId) {
            try {
                return primaryKeys.get(globalId);
            } catch (RuntimeException | ExecutionException e) {
                throw new JaversException(e.getCause());
            }
        }
    }
}
