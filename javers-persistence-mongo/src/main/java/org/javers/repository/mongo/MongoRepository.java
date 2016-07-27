package org.javers.repository.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.common.collections.Optional;
import org.javers.common.string.RegexEscape;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.json.typeadapter.date.DateTypeCoreAdapters;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.ValueObjectType;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.QueryParamsBuilder;
import org.javers.repository.api.SnapshotIdentifier;
import org.javers.repository.mongo.model.MongoHeadId;
import org.joda.time.LocalDateTime;

import java.util.*;

import static org.javers.common.validation.Validate.conditionFulfilled;
import static org.javers.repository.mongo.MongoSchemaManager.*;

/**
 * @author pawel szymczyk
 */
public class MongoRepository implements JaversRepository {

    private static final int DESC = -1;
    private final MongoSchemaManager mongoSchemaManager;
    private JsonConverter jsonConverter;

    public MongoRepository(MongoDatabase mongo) {
        this(mongo, null);
    }

    MongoRepository(MongoDatabase mongo, JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
        this.mongoSchemaManager = new MongoSchemaManager(mongo);
    }

    @Override
    public void persist(Commit commit) {
        persistSnapshots(commit);
        persistHeadId(commit);
    }

    void clean(){
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
        return queryForSnapshots(query, Optional.of(queryParams));
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        return getLatest(createIdQuery(globalId));
    }

    @Override
    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        return queryForSnapshots(new BasicDBObject(), Optional.of(queryParams));
    }

    @Override
    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return snapshotIdentifiers.isEmpty() ? Collections.<CdoSnapshot>emptyList() :
            queryForSnapshots(createSnapshotIdentifiersQuery(snapshotIdentifiers), Optional.<QueryParams>empty());
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        BasicDBObject query = new BasicDBObject(GLOBAL_ID_OWNER_ID_ENTITY, ownerEntity.getName());
        query.append(GLOBAL_ID_FRAGMENT, path);

        return queryForSnapshots(query, Optional.of(queryParams));
    }

    @Override
    public List<CdoSnapshot> getStateHistory(ManagedType givenClass, QueryParams queryParams) {
        Bson query = createManagedTypeQuery(givenClass, queryParams.isAggregate());
        return queryForSnapshots(query, Optional.of(queryParams));
    }

    @Override
    public CommitId getHeadId() {
        Document headId = headCollection().find().first();

        if (headId == null) {
            return null;
        }

        return new MongoHeadId(headId).toCommitId();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void ensureSchema() {
        mongoSchemaManager.ensureSchema();
    }

    private Bson createIdQuery(GlobalId id) {
        return new BasicDBObject(GLOBAL_ID_KEY, id.value());
    }

    private Bson createIdQueryWithAggregate(GlobalId id) {
        return Filters.or(createIdQuery(id), prefixQuery(GLOBAL_ID_KEY, id.value() + "#"));
    }

    private Bson createVersionQuery(Long version) {
        return new BasicDBObject(SNAPSHOT_VERSION, version);
    }

    private Bson createSnapshotIdentifiersQuery(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        Collection<Bson> descFilters = Lists.transform(new ArrayList<>(snapshotIdentifiers), new Function<SnapshotIdentifier, Bson>() {
            @Override
            public Bson apply(SnapshotIdentifier snapshotIdentifier) {
                return Filters.and(
                    createIdQuery(snapshotIdentifier.getGlobalId()),
                    createVersionQuery(snapshotIdentifier.getVersion())
                );
            }
        });
        return Filters.or(descFilters);
    }

    private Bson createManagedTypeQuery(ManagedType managedType, boolean aggregate) {
        if (managedType instanceof ValueObjectType) {
            return new BasicDBObject(GLOBAL_ID_VALUE_OBJECT, managedType.getName());
        }

        Bson query = prefixQuery(GLOBAL_ID_KEY, managedType.getName()+"/");

        if (!aggregate) { //only for EntityTypes entities
            query = Filters.and(query, Filters.exists(GLOBAL_ID_ENTITY));
        }

        return query;
    }

    private CdoSnapshot readFromDBObject(Document dbObject) {
        return jsonConverter.fromJson(dbObject.toJson(), CdoSnapshot.class);
    }

    private Document writeToDBObject(CdoSnapshot snapshot){
        conditionFulfilled(jsonConverter != null, "MongoRepository: jsonConverter is null");
        Document dbObject = Document.parse(jsonConverter.toJson(snapshot));
        dbObject.append(GLOBAL_ID_KEY,snapshot.getGlobalId().value());
        return dbObject;
    }

    private MongoCollection<Document> snapshotsCollection() {
        return mongoSchemaManager.snapshotsCollection();
    }

    private MongoCollection<Document> headCollection() {
        return mongoSchemaManager.headCollection();
    }

    private void persistSnapshots(Commit commit) {
        MongoCollection<Document> collection = snapshotsCollection();
        for (CdoSnapshot snapshot: commit.getSnapshots()) {
            collection.insertOne(writeToDBObject(snapshot));
        }
    }

    private void persistHeadId(Commit commit) {
        MongoCollection<Document> headIdCollection = headCollection();

        Document oldHead = headIdCollection.find().first();
        MongoHeadId newHeadId = new MongoHeadId(commit.getId());

        if (oldHead == null) {
            headIdCollection.insertOne(newHeadId.toDocument());
        } else {
            headIdCollection.updateOne(objectIdFiler(oldHead), newHeadId.getUpdateCommand());
        }
    }

    private Bson objectIdFiler(Document document) {
        return Filters.eq(OBJECT_ID, document.getObjectId("_id"));
    }

    private MongoCursor<Document> getMongoSnapshotsCursor(Bson query, Optional<QueryParams> queryParams) {
        FindIterable<Document> findIterable = snapshotsCollection()
            .find(applyQueryParams(query, queryParams))
            .sort(new Document(COMMIT_ID, DESC));
        return applyQueryParams(findIterable, queryParams).iterator();
    }

    private Bson applyQueryParams(Bson query, Optional<QueryParams> queryParams) {
        if (queryParams.isPresent()) {
            QueryParams params = queryParams.get();

            if (params.from().isPresent()) {
                query = addFromDateFiler(query, params.from().get());
            }
            if (params.to().isPresent()) {
                query = addToDateFilter(query, params.to().get());
            }
            if (params.commitId().isPresent()) {
                query = addCommitIdFilter(query, params.commitId().get());
            }
            if (params.version().isPresent()) {
                query = addVersionFilter(query, params.version().get());
            }
            if (params.author().isPresent()) {
                query = addAuthorFilter(query, params.author().get());
            }
            if (!params.commitProperties().isEmpty()) {
                query = addCommitPropertiesFilter(query, params.commitProperties());
            }
            if (params.changedProperty().isPresent()) {
                query = addChangedPropertyFilter(query, params.changedProperty().get());
            }
        }
        return query;
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

    private Bson addFromDateFiler(Bson query, LocalDateTime from) {
        return Filters.and(query, Filters.gte(COMMIT_DATE, DateTypeCoreAdapters.serialize(from)));
    }

    private Bson addToDateFilter(Bson query, LocalDateTime to) {
        return Filters.and(query, Filters.lte(COMMIT_DATE, DateTypeCoreAdapters.serialize(to)));
    }

    private Bson addCommitIdFilter(Bson query, CommitId commitId) {
        return Filters.and(query, new BasicDBObject(COMMIT_ID, commitId.valueAsNumber().doubleValue()));
    }

    private Bson addChangedPropertyFilter(Bson query, String changedProperty){
        return Filters.and(query, new BasicDBObject(CHANGED_PROPERTIES, changedProperty));
    }

    private Bson addVersionFilter(Bson query, Long version) {
        return Filters.and(query, createVersionQuery(version));
    }

    private Bson addCommitPropertiesFilter(Bson query, Map<String, String> commitProperties) {

        List<Bson> propertyFilters = new ArrayList<>();
        for (Map.Entry<String, String> commitProperty : commitProperties.entrySet()) {
            BasicDBObject propertyFilter = new BasicDBObject(COMMIT_PROPERTIES,
                new BasicDBObject("$elemMatch",
                    new BasicDBObject("key", commitProperty.getKey()).append(
                        "value", commitProperty.getValue())));
            propertyFilters.add(propertyFilter);
        }

        return Filters.and(query, Filters.and(propertyFilters.toArray(new Bson[]{})));
    }

    private Bson addAuthorFilter(Bson query, String author) {
        return Filters.and(query, new BasicDBObject(COMMIT_AUTHOR, author));
    }

    private Optional<CdoSnapshot> getLatest(Bson idQuery) {
        QueryParams queryParams = QueryParamsBuilder.withLimit(1).build();
        MongoCursor<Document> mongoLatest = getMongoSnapshotsCursor(idQuery, Optional.of(queryParams));

        if (!mongoLatest.hasNext()) {
            return Optional.empty();
        }

        Document dbObject = getOne(mongoLatest);
        return Optional.of(readFromDBObject(dbObject));
    }

    private List<CdoSnapshot> queryForSnapshots(Bson query, Optional<QueryParams> queryParams) {
        List<CdoSnapshot> snapshots = new ArrayList<>();
        try (MongoCursor<Document> mongoSnapshots = getMongoSnapshotsCursor(query, queryParams)) {
            while (mongoSnapshots.hasNext()) {
                Document dbObject = mongoSnapshots.next();
                snapshots.add(readFromDBObject(dbObject));
            }
            return snapshots;
        }
    }

    private static <T> T getOne(MongoCursor<T> mongoCursor){
        try{
            return mongoCursor.next();
        }
        finally {
            mongoCursor.close();
        }
    }

    //enables index range scan
    private static Bson prefixQuery(String fieldName, String prefix){
        return Filters.regex(fieldName, "^" + RegexEscape.escape(prefix) + ".*");
    }
}