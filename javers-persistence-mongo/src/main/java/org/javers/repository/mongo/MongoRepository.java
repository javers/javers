package org.javers.repository.mongo;

import static com.mongodb.client.model.Sorts.orderBy;
import static com.mongodb.client.model.Sorts.descending;
import com.mongodb.client.*;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.javers.common.string.RegexEscape;
import org.javers.common.validation.Validate;
import org.javers.core.CommitIdGenerator;
import org.javers.core.CoreConfiguration;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.ValueObjectType;
import org.javers.repository.api.*;
import org.javers.repository.mongo.model.MongoHeadId;

import java.util.*;
import java.util.stream.Collectors;

import static org.javers.common.collections.Lists.toImmutableList;
import static org.javers.common.validation.Validate.conditionFulfilled;
import static org.javers.repository.mongo.DocumentConverter.fromDocument;
import static org.javers.repository.mongo.DocumentConverter.toDocument;
import static org.javers.repository.mongo.MongoDialect.DOCUMENT_DB;
import static org.javers.repository.mongo.MongoRepositoryConfigurationBuilder.mongoRepositoryConfiguration;
import static org.javers.repository.mongo.MongoSchemaManager.*;

/**
 * MongoDB implementation of {@link JaversRepository}.
 * <br/><br/>
 *
 * Joins the Mongo transaction started by the application, if a {@link MongoSessionProvider} is given.
 * Without it, reads and writes are done outside any transaction.
 * For Spring transactions see <code>TransactionalMongoRepositoryBuilder</code>.
 */
public class MongoRepository implements JaversRepository, ConfigurationAware {
    private final static double COMMIT_ID_PRECISION = 0.005;

    private final MongoDatabase mongo;
    private final MongoRepositoryConfiguration configuration;
    private final MongoSessionProvider sessionProvider;
    private final MongoSchemaManager mongoSchemaManager;
    private JsonConverter jsonConverter;
    private CoreConfiguration coreConfiguration;
    private final MapKeyDotReplacer mapKeyDotReplacer = new MapKeyDotReplacer();
    private final LatestSnapshotCache latestSnapshotCache;
    private final MongoDialect mongoDialect;
    private final boolean schemaManagementEnabled;
    private final boolean cacheDisabled;

    public MongoRepository(MongoDatabase mongo) {
        this(mongo, mongoRepositoryConfiguration().build());
    }

    /**
     * MongoRepository compatible with Amazon DocumentDB.
     * <br/>
     *
     * Compound index on <code>commitProperties</code> isn't created.
     * <br/><br/>
     *
     * See <a href="http://docs.aws.amazon.com/documentdb/latest/developerguide/functional-differences.html">functional differences</a>.
     */
    public static MongoRepository mongoRepositoryWithDocumentDBCompatibility(MongoDatabase mongo, int cacheSize) {
        return new MongoRepository(mongo, mongoRepositoryConfiguration()
                .withDialect(DOCUMENT_DB)
                .withCacheSize(cacheSize).build());
    }

    /**
     * @param cacheSize Size of the latest snapshots cache, default is 5000. Set 0 to disable.
     */
    public MongoRepository(MongoDatabase mongo, int cacheSize) {
        this(mongo, mongoRepositoryConfiguration().withCacheSize(cacheSize).build());
    }

    MongoRepository(MongoDatabase mongo, int cacheSize, MongoDialect dialect) {
        this(mongo, mongoRepositoryConfiguration()
                .withDialect(dialect)
                .withCacheSize(cacheSize).build());
    }

    public MongoRepository(MongoDatabase mongo, MongoRepositoryConfiguration mongoRepositoryConfiguration) {
        this(mongo, mongoRepositoryConfiguration, MongoSessionProvider.noTransactions());
    }

    /**
     * @param sessionProvider provides the application's Mongo transaction, which this repository joins
     * @since 7.12
     */
    public MongoRepository(MongoDatabase mongo, MongoRepositoryConfiguration config, MongoSessionProvider sessionProvider) {
        Validate.argumentsAreNotNull(mongo);
        Validate.argumentsAreNotNull(config);
        Validate.argumentsAreNotNull(sessionProvider);
        this.mongo = mongo;
        this.configuration = config;
        this.sessionProvider = sessionProvider;
        this.mongoDialect = config.getMongoDialect();
        this.mongoSchemaManager = new MongoSchemaManager(
            mongo, config.getSnapshotCollectionName(),
            config.getHeadCollectionName());
        int cacheSize = config.getCacheSize();
        this.cacheDisabled = cacheSize == 0;
        this.latestSnapshotCache = new LatestSnapshotCache(cacheSize, input -> getLatest(createIdQuery(input), Optional.empty()));
        this.schemaManagementEnabled = config.isSchemaManagementEnabled();
    }

    /**
     * Creates a repository on the same database and configuration as the source, with its own cache.
     */
    protected MongoRepository(MongoRepository source, MongoSessionProvider sessionProvider) {
        this(source.mongo, source.configuration, sessionProvider);
    }

    @Override
    public void persist(Commit commit) {
        Optional<MongoTransaction> tx = sessionProvider.currentTransaction();
        Optional<ClientSession> session = tx.map(MongoTransaction::clientSession);
        persistSnapshots(commit, session);
        persistHeadId(commit, session);

        if (!cacheDisabled) {
            tx.ifPresentOrElse(
                    t -> t.cacheLocallyAndPromoteOnCommit(commit, latestSnapshotCache),
                    () -> cacheCommitted(commit));
        }
    }

    /**
     * Writes the commit with the given session, without caching it.
     * For a caller which runs its own transaction and caches the commit
     * with {@link #cacheCommitted(Commit)} after the transaction has committed.
     */
    protected void persistToDb(Commit commit, ClientSession clientSession) {
        persistSnapshots(commit, Optional.of(clientSession));
        persistHeadId(commit, Optional.of(clientSession));
    }

    /**
     * Puts snapshots of an already committed commit to the latest-snapshot cache (L2).
     */
    protected void cacheCommitted(Commit commit) {
        latestSnapshotCache.putAll(commit);
    }

    /**
     * <b>Warning! Do not use this method!</b>
     *
     * <br/>
     * Snapshots are put to the latest-snapshot cache right away, before the transaction commits,
     * so they stay in the cache even if the transaction is rolled back (which is a bad thing).
     *
     * @deprecated pass a {@link MongoSessionProvider} to the constructor and use {@link #persist(Commit)},
     *             which caches snapshots only after the transaction commits
     */
    @Deprecated
    public void persist(Commit commit, ClientSession clientSession) {
        persistSnapshots(commit, Optional.of(clientSession));
        persistHeadId(commit, Optional.of(clientSession));
        latestSnapshotCache.putAll(commit);
    }

    void clean() {
        snapshotsCollection().deleteMany(new Document());
        headCollection().deleteMany(new Document());
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        Bson query;
        if (queryParams.isAggregate()){
            query = createIdQueryWithAggregate(globalId);
        } else {
            query = createIdQuery(globalId);
        }
        return queryForSnapshots(query, Optional.of(queryParams), currentSession());
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        Optional<MongoTransaction> tx = sessionProvider.currentTransaction();
        if (cacheDisabled) {
            return getLatestFromDb(globalId, tx);
        }
        if (tx.isEmpty()) {
            return latestSnapshotCache.getLatest(globalId);
        }
        return getLatestFromCacheTransactionAware(globalId, tx.get());
    }

    /**
     * Snapshots written by this transaction come from L1.
     * If L1 dropped one (overflow), L2 may hold its older version,
     * so it's read from the database with the transaction's session.
     * Entities not written by this transaction come from L2.
     */
    private Optional<CdoSnapshot> getLatestFromCacheTransactionAware(GlobalId globalId, MongoTransaction tx) {
        TransactionLocalLatestSnapshotCache l1 = tx.localLatestSnapshotCache().orElse(null);
        if (l1 == null) {
            // nothing written in this transaction yet
            return latestSnapshotCache.getLatest(globalId);
        }
        Optional<CdoSnapshot> fromL1 = l1.get(globalId);
        if (fromL1.isPresent()) {
            return fromL1;
        }
        if (l1.isOverflowed(globalId)) {
            return getLatestFromDb(globalId, Optional.of(tx));
        }
        return latestSnapshotCache.getLatest(globalId);
    }

    private Optional<CdoSnapshot> getLatestFromDb(GlobalId globalId, Optional<MongoTransaction> tx) {
        return getLatest(createIdQuery(globalId), tx.map(MongoTransaction::clientSession));
    }

    @Override
    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        return queryForSnapshots(new BasicDBObject(), Optional.of(queryParams), currentSession());
    }

    @Override
    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return snapshotIdentifiers.isEmpty() ? Collections.emptyList() :
            queryForSnapshots(createSnapshotIdentifiersQuery(snapshotIdentifiers), Optional.empty(), currentSession());
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        BasicDBObject query = new BasicDBObject(GLOBAL_ID_OWNER_ID_ENTITY, ownerEntity.getName());
        query.append(GLOBAL_ID_FRAGMENT, path);
        return queryForSnapshots(query, Optional.of(queryParams), currentSession());
    }

    @Override
    public List<CdoSnapshot> getStateHistory(Set<ManagedType> givenClasses, QueryParams queryParams) {
        Bson query = createManagedTypeQuery(givenClasses, queryParams.isAggregate());
        return queryForSnapshots(query, Optional.of(queryParams), currentSession());
    }

    /**
     * Always reads outside the transaction, only committed commits count.
     */
    @Override
    public CommitId getHeadId() {
        Document headId = headCollection().find().first();
        if (headId == null) {
            return null;
        }
        return new MongoHeadId(headId).toCommitId();
    }

    /**
     * Stats of the shared (L2) latest snapshots cache, which holds only committed data.
     */
    public CacheStats latestSnapshotCacheStats() {
        return latestSnapshotCache.stats();
    }

    /**
     * Stats of the transaction-local latest snapshot cache (L1) of this repository in the current transaction.
     */
    public CacheStats currentTransactionCacheStats() {
        return sessionProvider.currentTransaction()
                .flatMap(MongoTransaction::localLatestSnapshotCache)
                .map(TransactionLocalLatestSnapshotCache::stats)
                .orElseGet(() -> CacheStats.l1(0, 0, 0));
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void setConfiguration(CoreConfiguration coreConfiguration) {
        this.coreConfiguration = coreConfiguration;
    }

    @Override
    public void ensureSchema() {
        if (schemaManagementEnabled) {
            mongoSchemaManager.ensureSchema(mongoDialect);
        }
    }

    private Optional<ClientSession> currentSession() {
        return sessionProvider.currentTransaction().map(MongoTransaction::clientSession);
    }

    private Bson createIdQuery(GlobalId id) {
        return new BasicDBObject(GLOBAL_ID_KEY, id.value());
    }

    private Bson createIdQueryWithAggregate(GlobalId id) {
        if (id instanceof InstanceId) {
            Object cdoId = ((InstanceId)id).getCdoId();
            if (cdoId instanceof UUID) {
                UUID uuidCdoId = (UUID) cdoId;
                cdoId = uuidCdoId.toString();
            }
            return Filters.or(
                createIdQuery(id),
                new BasicDBObject(GLOBAL_ID_OWNER_ID_CDO_ID, cdoId)
            );
        }

        return Filters.or(
            createIdQuery(id),
            prefixQuery(GLOBAL_ID_KEY, id.value() + "#")
        );
    }

    private Bson createVersionQuery(Long version) {
        return new BasicDBObject(SNAPSHOT_VERSION, version);
    }

    private Bson createSnapshotIdentifiersQuery(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        List<Bson> descFilters = snapshotIdentifiers.stream().map(
            snapshotIdentifier -> Filters.and(
                createIdQuery(snapshotIdentifier.getGlobalId()),
                createVersionQuery(snapshotIdentifier.getVersion())
        )).collect(toImmutableList());
        return Filters.or(descFilters);
    }

    private Bson createManagedTypeQuery(Set<ManagedType> managedTypes, boolean aggregate) {
        List<Bson> classFilters = managedTypes.stream().map( managedType -> {
                if (managedType instanceof ValueObjectType) {
                    return createValueObjectTypeQuery(managedType);
                } else {
                    return createEntityTypeQuery(aggregate, managedType);
                }
            }).collect(toImmutableList());
        return Filters.or(classFilters);
    }

    private Bson createValueObjectTypeQuery(ManagedType managedType) {
        return new BasicDBObject(GLOBAL_ID_VALUE_OBJECT, managedType.getName());
    }

    private Bson createEntityTypeQuery(boolean aggregate, ManagedType managedType) {
        Bson entityTypeQuery = prefixQuery(GLOBAL_ID_KEY, managedType.getName() + "/");
        if (!aggregate) {
            entityTypeQuery = Filters.and(entityTypeQuery, Filters.exists(GLOBAL_ID_ENTITY));
        }
        return entityTypeQuery;
    }

    private CdoSnapshot readFromDBObject(Document dbObject) {
        return jsonConverter.fromJson(fromDocument(mapKeyDotReplacer.back(dbObject)), CdoSnapshot.class);
    }

    private Document writeToDBObject(CdoSnapshot snapshot) {
        conditionFulfilled(jsonConverter != null, "MongoRepository: jsonConverter is null");
        Document dbObject = toDocument((JsonObject)jsonConverter.toJsonElement(snapshot));
        dbObject = mapKeyDotReplacer.replaceInSnapshotState(dbObject);
        dbObject.append(GLOBAL_ID_KEY, snapshot.getGlobalId().value());
        return dbObject;
    }

    private MongoCollection<Document> snapshotsCollection() {
        return mongoSchemaManager.snapshotsCollection();
    }

    private MongoCollection<Document> headCollection() {
        return mongoSchemaManager.headCollection();
    }

    private void persistSnapshots(Commit commit, Optional<ClientSession> clientSession) {
        MongoCollection<Document> collection = snapshotsCollection();
        commit.getSnapshots().forEach(snapshot -> {
            transactionalInsert(collection, writeToDBObject(snapshot), clientSession);
        });
    }

    private void persistHeadId(Commit commit, Optional<ClientSession> clientSession) {
        if (coreConfiguration.getCommitIdGenerator() != CommitIdGenerator.SYNCHRONIZED_SEQUENCE) {
            return;
        }

        MongoCollection<Document> headIdCollection = headCollection();

        Document oldHead = clientSession
                .map(s -> headIdCollection.find(s).first())
                .orElseGet(() -> headIdCollection.find().first());
        MongoHeadId newHeadId = new MongoHeadId(commit.getId());

        if (oldHead == null) {
            transactionalInsert(headIdCollection, newHeadId.toDocument(), clientSession);
        } else {
            transactionalUpdate(headIdCollection, objectIdFiler(oldHead), newHeadId.getUpdateCommand(), clientSession);
        }
    }

    private Bson objectIdFiler(Document document) {
        return Filters.eq(OBJECT_ID, document.getObjectId("_id"));
    }

    private MongoCursor<Document> getMongoSnapshotsCursor(Bson query, Optional<QueryParams> queryParams, Optional<ClientSession> clientSession) {
        final MongoCollection<Document> collection = snapshotsCollection();
        final Bson filter = applyQueryParams(query, queryParams);

        FindIterable<Document> findIterable = clientSession
                .map(session -> collection.find(session, filter))
                .orElseGet(() -> collection.find(filter));

        if (coreConfiguration.getCommitIdGenerator() == CommitIdGenerator.SYNCHRONIZED_SEQUENCE) {
            findIterable.sort(orderBy(descending(COMMIT_ID, OBJECT_ID)));
        }
        else {
            findIterable.sort(orderBy(descending(COMMIT_DATE_INSTANT, OBJECT_ID)));
        }

        return applyQueryParams(findIterable, queryParams).iterator();
    }

    private Bson applyQueryParams(Bson query, Optional<QueryParams> queryParams) {
        if (queryParams.isPresent()) {
            QueryParams params = queryParams.get();

            if (params.from().isPresent()) {
                query = Filters.and(query, Filters.gte(COMMIT_DATE, UtilTypeCoreAdapters.serialize(params.from().get())));
            }
            if (params.fromInstant().isPresent()) {
                query = Filters.and(query, Filters.gte(COMMIT_DATE_INSTANT, UtilTypeCoreAdapters.serialize(params.fromInstant().get())));
            }
            if (params.to().isPresent()) {
                query =  Filters.and(query, Filters.lte(COMMIT_DATE, UtilTypeCoreAdapters.serialize(params.to().get())));
            }
            if (params.toInstant().isPresent()) {
                query = Filters.and(query, Filters.lte(COMMIT_DATE_INSTANT, UtilTypeCoreAdapters.serialize(params.toInstant().get())));
            }
            if (params.toCommitId().isPresent()) {
                query = Filters.and(query, Filters.lte(COMMIT_ID, params.toCommitId().get().valueAsNumber().doubleValue() + COMMIT_ID_PRECISION));
            }
            if (params.commitIds().size() > 0) {
                query = Filters.or(params.commitIds().stream()
                        .map(it -> commitIdFilter(it)).collect(Collectors.toList()));
            }
            if (params.version().isPresent()) {
                query = Filters.and(query, createVersionQuery(params.version().get()));
            }
            if (params.fromVersion().isPresent()) {
                query = Filters.and(query, Filters.gte(SNAPSHOT_VERSION, params.fromVersion().get()));
            }
            if (params.toVersion().isPresent()) {
                query = Filters.and(query, Filters.lte(SNAPSHOT_VERSION, params.toVersion().get()));
            }
            if (params.author().isPresent()) {
                query = Filters.and(query, new BasicDBObject(COMMIT_AUTHOR, params.author().get()));
            }
            if (params.authorLikeIgnoreCase().isPresent()) {
                query = Filters.and(query, new BasicDBObject(COMMIT_AUTHOR,
                        new BasicDBObject("$regex", ".*" + params.authorLikeIgnoreCase().get().toLowerCase(Locale.ROOT) + ".*").append("$options", "i")));
            }
            if (!params.commitProperties().isEmpty()) {
                query = addCommitPropertiesFilter(query, params.commitProperties());
            }
            if (!params.commitPropertiesLike().isEmpty()) {
                query = addCommitPropertiesLikeFilter(query, params.commitPropertiesLike());
            }
            if (params.changedProperties().size() > 0) {
                query = Filters.and(query, Filters.or(params.changedProperties().stream()
                        .map(it -> new BasicDBObject(CHANGED_PROPERTIES, it)).collect(Collectors.toList())));
            }
            if (params.snapshotType().isPresent()) {
                query = Filters.and(query, new BasicDBObject(SNAPSHOT_TYPE, params.snapshotType().get().name()));
            }

        }
        return query;
    }

    private Bson commitIdFilter(CommitId commitId) {
        if (commitId.getMinorId() > 0) {
            commitId.valueAsNumber().doubleValue();
            return Filters.and(
                    Filters.gte(COMMIT_ID, commitId.valueAsNumber().doubleValue() - COMMIT_ID_PRECISION),
                    Filters.lte(COMMIT_ID, commitId.valueAsNumber().doubleValue() + COMMIT_ID_PRECISION)
            );
        }
        return Filters.eq(COMMIT_ID, commitId.getMajorId());
    }

    private FindIterable<Document> applyQueryParams(FindIterable<Document> findIterable, Optional<QueryParams> queryParams) {
        if (queryParams.isPresent()) {
            QueryParams params = queryParams.get();
            findIterable = findIterable
                .limit(params.limit())
                .skip(params.skip());
        }
        return  findIterable;
    }

    private Bson addCommitPropertiesFilter(Bson query, Map<String, Collection<String>> commitProperties) {
        List<Bson> propertyFilters = commitProperties.entrySet().stream().map( commitProperty ->
            new BasicDBObject(COMMIT_PROPERTIES,
                new BasicDBObject("$elemMatch",
                        new BasicDBObject("key", commitProperty.getKey()).append(
                                          "value",new BasicDBObject("$in",commitProperty.getValue()))))
        ).collect(toImmutableList());
        return Filters.and(query, Filters.and(propertyFilters.toArray(new Bson[]{})));
    }

    private Bson addCommitPropertiesLikeFilter(Bson query, Map<String, String> commitProperties) {
        List<Bson> propertyFilters = commitProperties.entrySet().stream().map(commitProperty ->
                new BasicDBObject(COMMIT_PROPERTIES,
                        new BasicDBObject("$elemMatch",
                                new BasicDBObject("key", commitProperty.getKey())
                                        .append("value", new BasicDBObject("$regex",".*" + commitProperty.getValue().toLowerCase(Locale.ROOT) + ".*").append("$options", "i"))))
        ).collect(toImmutableList());
        return Filters.and(query, Filters.and(propertyFilters.toArray(new Bson[]{})));
    }

    private Optional<CdoSnapshot> getLatest(Bson idQuery, Optional<ClientSession> clientSession) {
        QueryParams queryParams = QueryParamsBuilder.withLimit(1).build();
        MongoCursor<Document> mongoLatest = getMongoSnapshotsCursor(idQuery, Optional.of(queryParams), clientSession);
        return getOne(mongoLatest).map(d -> readFromDBObject(d));
    }

    private List<CdoSnapshot> queryForSnapshots(Bson query, Optional<QueryParams> queryParams, Optional<ClientSession> clientSession) {
        List<CdoSnapshot> snapshots = new ArrayList<>();
        try (MongoCursor<Document> mongoSnapshots = getMongoSnapshotsCursor(query, queryParams, clientSession)) {
            while (mongoSnapshots.hasNext()) {
                Document dbObject = mongoSnapshots.next();
                snapshots.add(readFromDBObject(dbObject));
            }
            return snapshots;
        }
    }

    private static <T> Optional<T> getOne(MongoCursor<T> mongoCursor) {
        try {
            if (!mongoCursor.hasNext()) {
                return Optional.empty();
            }
            return Optional.of(mongoCursor.next());
        }
        finally {
            mongoCursor.close();
        }
    }

    private static Bson prefixQuery(String fieldName, String prefix) {
        return Filters.regex(fieldName, "^" + RegexEscape.escape(prefix) + ".*");
    }

    private void transactionalInsert(
            MongoCollection<Document> collection,
            Document document,
            Optional<ClientSession> clientSession) {
        clientSession.map(s -> collection.insertOne(s, document))
                .orElseGet(() -> collection.insertOne(document));
    }

    private void transactionalUpdate(
            MongoCollection<Document> collection,
            Bson filter, Bson update,
            Optional<ClientSession> clientSession) {
        clientSession.map(s -> collection.updateOne(s, filter, update))
                .orElseGet(() -> collection.updateOne(filter, update));
    }
}
