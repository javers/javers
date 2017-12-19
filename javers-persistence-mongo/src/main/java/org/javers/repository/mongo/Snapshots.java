package org.javers.repository.mongo;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.javers.repository.mongo.MongoConstants.KEY_PROPERTY;

class Snapshots {
    private static final Logger log = LoggerFactory.getLogger(Snapshots.class);

    private final MongoCollection<Document> snapshots;

    Snapshots(MongoCollection<Document> snapshots) {
        this.snapshots = snapshots;
    }

    void dropGlobalIdEntityIndex() {
        for (Document document : snapshots.listIndexes()) {
            Document key = document.get(KEY_PROPERTY, Document.class);
            if (key.containsKey(MongoSchemaManager.GLOBAL_ID_ENTITY)) {
                log.warn("Schema migration. Dropping index: {}...", MongoSchemaManager.GLOBAL_ID_ENTITY);
                try {
                    snapshots.dropIndex(MongoSchemaManager.GLOBAL_ID_ENTITY);
                } catch (Exception e) {
                    log.debug("failed to drop index " + MongoSchemaManager.GLOBAL_ID_ENTITY, e);
                }
                return;
            }
        }
    }
}