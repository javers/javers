package org.javers.repository.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.javers.repository.mongo.model.MongoHeadId;

import java.util.function.Function;

/**
 * @author bartosz.walacik
 */
class MongoSchemaManager {
    static final int ASC = 1;
    static final String SNAPSHOTS = "jv_snapshots";
    static final String COMMIT_ID = "commitMetadata.id";
    static final String COMMIT_DATE = "commitMetadata.commitDate";
    static final String COMMIT_AUTHOR = "commitMetadata.author";
    static final String COMMIT_PROPERTIES = "commitMetadata.properties";
    static final String GLOBAL_ID_KEY = "globalId_key";
    static final String GLOBAL_ID_ENTITY = "globalId.entity";
    static final String GLOBAL_ID_OWNER_ID_ENTITY = "globalId.ownerId.entity";
    static final String GLOBAL_ID_FRAGMENT = "globalId.fragment";
    static final String GLOBAL_ID_VALUE_OBJECT = "globalId.valueObject";
    static final String SNAPSHOT_VERSION = "version";
    static final String CHANGED_PROPERTIES = "changedProperties";
    static final String OBJECT_ID = "_id";
    static final String SNAPSHOT_TYPE = "type";

    private final MongoDatabase mongo;
    private final boolean useTypeNameIndex;
    private final Function<MongoCollection<Document>, Snapshots> factory;
    private final CommitIdTypeMigration migration;

    MongoSchemaManager(MongoDatabase mongo, boolean useTypeNameIndex) {
        this(mongo, useTypeNameIndex, Snapshots::new, new CommitIdTypeMigration());
    }

    MongoSchemaManager(MongoDatabase mongo, boolean useTypeNameIndex,
                       Function<MongoCollection<Document>, Snapshots> factory, CommitIdTypeMigration migration) {
        this.mongo = mongo;
        this.useTypeNameIndex = useTypeNameIndex;
        this.factory = factory;
        this.migration = migration;
    }

    public void ensureSchema() {
        //ensures collections and indexes
        MongoCollection<Document> documents = snapshotsCollection();
        documents.createIndex(new BasicDBObject(GLOBAL_ID_KEY, ASC));
        documents.createIndex(new BasicDBObject(GLOBAL_ID_VALUE_OBJECT, ASC));
        documents.createIndex(new BasicDBObject(GLOBAL_ID_OWNER_ID_ENTITY, ASC));
        documents.createIndex(new BasicDBObject(CHANGED_PROPERTIES, ASC));
        documents.createIndex(new BasicDBObject(COMMIT_PROPERTIES + ".key", ASC).append(COMMIT_PROPERTIES + ".value", ASC));

        headCollection();

        if (!useTypeNameIndex) {
            //schema migration script from JaVers 2.0 to 2.1
            Snapshots snapshots = factory.apply(documents);
            snapshots.dropGlobalIdEntityIndex();
        }

        //schema migration script from JaVers 1.1 to 1.2
        migration.runOnInstance(mongo);
    }

    MongoCollection<Document> snapshotsCollection() {
        return mongo.getCollection(SNAPSHOTS);
    }

    MongoCollection<Document> headCollection() {
        return mongo.getCollection(MongoHeadId.COLLECTION_NAME);
    }
}
