package org.javers.repository.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;
import org.javers.repository.mongo.model.MongoHeadId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.javers.repository.mongo.MongoDialect.DOCUMENT_DB;
import static org.javers.repository.mongo.MongoDialect.MONGO_DB;

/**
 * @author bartosz.walacik
 */
class MongoSchemaManager {
    static final int ASC = 1;
    static final String SNAPSHOTS = "jv_snapshots";
    static final String COMMIT_ID = "commitMetadata.id";
    static final String COMMIT_DATE = "commitMetadata.commitDate";
    static final String COMMIT_DATE_INSTANT = "commitMetadata.commitDateInstant";
    static final String COMMIT_AUTHOR = "commitMetadata.author";
    static final String COMMIT_PROPERTIES = "commitMetadata.properties";
    static final String COMMIT_PROPERTIES_INDEX_NAME = "commitMetadata.properties_key_value";
    static final String GLOBAL_ID_KEY = "globalId_key";
    static final String GLOBAL_ID_ENTITY = "globalId.entity";
    static final String GLOBAL_ID_OWNER_ID_ENTITY = "globalId.ownerId.entity";
    static final String GLOBAL_ID_FRAGMENT = "globalId.fragment";
    static final String GLOBAL_ID_VALUE_OBJECT = "globalId.valueObject";
    static final String SNAPSHOT_VERSION = "version";
    static final String CHANGED_PROPERTIES = "changedProperties";
    static final String OBJECT_ID = "_id";
    static final String SNAPSHOT_TYPE = "type";

    private static final Logger logger = LoggerFactory.getLogger(MongoSchemaManager.class);

    private final MongoDatabase mongo;

    MongoSchemaManager(MongoDatabase mongo) {
        this.mongo = mongo;
    }

    public void ensureSchema(MongoDialect dialect) {
        //ensures collections and indexes
        MongoCollection<Document> snapshots = snapshotsCollection();
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_KEY, ASC));
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_VALUE_OBJECT, ASC));
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_ENTITY, ASC));
        snapshots.createIndex(new BasicDBObject(GLOBAL_ID_OWNER_ID_ENTITY, ASC));
        snapshots.createIndex(new BasicDBObject(CHANGED_PROPERTIES, ASC));

        if (dialect == MONGO_DB) {
            snapshots.createIndex(new BasicDBObject(COMMIT_PROPERTIES + ".key", ASC).append(COMMIT_PROPERTIES + ".value", ASC),
                    new IndexOptions().name(COMMIT_PROPERTIES_INDEX_NAME));
        }
        else if (dialect == DOCUMENT_DB) {
            snapshots.createIndex(new BasicDBObject(COMMIT_PROPERTIES + ".key", ASC));
            snapshots.createIndex(new BasicDBObject(COMMIT_PROPERTIES + ".value", ASC));
        }

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

    MongoCollection<Document> snapshotsCollection() {
        return mongo.getCollection(SNAPSHOTS);
    }

    MongoCollection<Document> headCollection() {
        return mongo.getCollection(MongoHeadId.COLLECTION_NAME);
    }
}
