package org.javers.repository.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.json.typeadapter.date.DateTypeAdapters;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.ValueObjectType;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.QueryParamsBuilder;
import org.javers.repository.mongo.model.MongoHeadId;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.javers.common.validation.Validate.conditionFulfilled;

/**
 * @author pawel szymczyk
 */
public class MongoRepository implements JaversRepository {
    private static final Logger logger = LoggerFactory.getLogger(MongoRepository.class);

    private static final int DESC = -1;
    private static final int ASC = 1;
    public static final String SNAPSHOTS = "jv_snapshots";
    public static final String COMMIT_ID = "commitMetadata.id";
    public static final String COMMIT_DATE = "commitMetadata.commitDate";
    public static final String GLOBAL_ID_KEY = "globalId_key";
    public static final String GLOBAL_ID_ENTITY = "globalId.entity";
    public static final String GLOBAL_ID_OWNER_ID_ENTITY = "globalId.ownerId.entity";
    public static final String GLOBAL_ID_FRAGMENT = "globalId.fragment";
    public static final String GLOBAL_ID_VALUE_OBJECT = "globalId.valueObject";
    public static final String CHANGED_PROPERTIES = "changedProperties";
    public static final String OBJECT_ID = "_id";

    private MongoDatabase mongo;
    private JsonConverter jsonConverter;

    public MongoRepository(MongoDatabase mongo) {
        this.mongo = mongo;
    }

    MongoRepository(MongoDatabase mongo, JsonConverter jsonConverter) {
        this.mongo = mongo;
        this.jsonConverter = jsonConverter;
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
        return queryForSnapshots(createIdQuery(globalId), queryParams);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        return getLatest(createIdQuery(globalId));
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        BasicDBObject query = new BasicDBObject(GLOBAL_ID_OWNER_ID_ENTITY, ownerEntity.getName());
        query.append(GLOBAL_ID_FRAGMENT, path);

        return queryForSnapshots(query, queryParams);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(GlobalId globalId, String propertyName, QueryParams queryParams) {
        BasicDBObject query = createIdQuery(globalId);

        query.append(CHANGED_PROPERTIES, propertyName);

        return queryForSnapshots(query, queryParams);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(ManagedType givenClass, String propertyName, QueryParams queryParams) {
        BasicDBObject query = createGlobalIdClassQuery(givenClass);

        query.append(CHANGED_PROPERTIES, propertyName);

        return queryForSnapshots(query, queryParams);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(ManagedType givenClass, QueryParams queryParams) {
        BasicDBObject query = createGlobalIdClassQuery(givenClass);
        return queryForSnapshots(query, queryParams);
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
        //ensures collections and indexes
        MongoCollection<Document> snapshots = snapshotsCollection();
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_KEY, ASC));
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_ENTITY, ASC));
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_VALUE_OBJECT, ASC));
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_OWNER_ID_ENTITY, ASC));
        snapshots.createIndex(new BasicDBObject(CHANGED_PROPERTIES, ASC));
        headCollection();

        //schema migration script from JaVers 1.1 to 1.2
        Document doc = snapshots.find().first();
        if (doc != null) {
            Object stringCommitId = ((Map)doc.get("commitMetadata")).get("id");
            if (stringCommitId instanceof String) {
                logger.info("executing db migration script, from JaVers 1.1 to 1.2 ...");

                Document update = new Document("eval",
                    "function() { \n"+
                            "    db.jv_snapshots.find().forEach( \n"+
                            "      function(snapshot) { \n"+
                            "        snapshot.commitMetadata.id = Number(snapshot.commitMetadata.id); \n"+
                            "        db.jv_snapshots.save(snapshot); } \n" +
                            "    ); "+
                            "    return 'ok'; \n"+
                            "}"
                    );

                Document ret = mongo.runCommand(update);
                logger.info("result: \n "+ ret.toJson());
            }
        }
    }

    private BasicDBObject createIdQuery(GlobalId id) {
        return new BasicDBObject (GLOBAL_ID_KEY, id.value());
    }

    private BasicDBObject createFromQuery(LocalDateTime from) {
        return new BasicDBObject (COMMIT_DATE, new BasicDBObject("$gte", DateTypeAdapters.serialize(from)));
    }

    private BasicDBObject createToQuery(LocalDateTime to) {
        return new BasicDBObject (COMMIT_DATE, new BasicDBObject("$lte", DateTypeAdapters.serialize(to)));
    }

    private BasicDBObject createGlobalIdClassQuery(ManagedType givenClass) {
        String cName = givenClass.getName();

        BasicDBObject query = null;
        if (givenClass instanceof EntityType) {
            query = new BasicDBObject(GLOBAL_ID_ENTITY, cName);
        }
        if (givenClass instanceof ValueObjectType) {
            query = new BasicDBObject(GLOBAL_ID_VALUE_OBJECT, cName);
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
        return mongo.getCollection(SNAPSHOTS);
    }

    private MongoCollection<Document> headCollection() {
        return mongo.getCollection(MongoHeadId.COLLECTION_NAME);
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

    private MongoCursor<Document> getMongoSnapshotsCursor(Bson idQuery, QueryParams queryParams) {
        Bson query = applyQueryParams(idQuery, queryParams);
        int limit = queryParams.getLimit();
        return snapshotsCollection()
                .find(query).sort(new Document(COMMIT_ID, DESC)).limit(limit).iterator();
    }

    private Bson applyQueryParams(Bson query, QueryParams queryParams) {
        if (queryParams.from().isPresent()) {
            query = Filters.and(query, createFromQuery(queryParams.from().get()));
        }
        if (queryParams.to().isPresent()) {
            query = Filters.and(query, createToQuery(queryParams.to().get()));
        }
        return query;
    }

    private Optional<CdoSnapshot> getLatest(Bson idQuery) {
        QueryParams queryParams = QueryParamsBuilder.withLimit(1).build();
        MongoCursor<Document> mongoLatest = getMongoSnapshotsCursor(idQuery, queryParams);

        if (!mongoLatest.hasNext()) {
            return Optional.empty();
        }

        Document dbObject = getOne(mongoLatest);
        return Optional.of(readFromDBObject(dbObject));
    }

    private List<CdoSnapshot> queryForSnapshots(Bson query, QueryParams queryParams) {
        List<CdoSnapshot> snapshots = new ArrayList<>();
        try (MongoCursor<Document> mongoSnapshots = getMongoSnapshotsCursor(query, queryParams)) {
            while (mongoSnapshots.hasNext()) {
                Document dbObject = mongoSnapshots.next();
                snapshots.add(readFromDBObject(dbObject));
            }
            return snapshots;
        }
    }

    private <T> T getOne(MongoCursor<T> mongoCursor){
        try{
            return mongoCursor.next();
        }
        finally {
            mongoCursor.close();
        }
    }
}