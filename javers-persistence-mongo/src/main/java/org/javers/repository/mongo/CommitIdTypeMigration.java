package org.javers.repository.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

import static org.javers.repository.mongo.MongoConstants.COMMIT_ID_PROPERTY;
import static org.javers.repository.mongo.MongoConstants.COMMIT_METADATA_PROPERTY;
import static org.javers.repository.mongo.MongoConstants.EVAL_KEY;
import static org.javers.repository.mongo.MongoSchemaManager.SNAPSHOTS;

class CommitIdTypeMigration {
    private static final Logger log = LoggerFactory.getLogger(CommitIdTypeMigration.class);
    private static final String UPDATE_FUNCTION = "function() { \n" +
            "    db.jv_snapshots.find().forEach( \n" +
            "      function(snapshot) { \n" +
            "        snapshot.commitMetadata.id = Number(snapshot.commitMetadata.id); \n" +
            "        db.jv_snapshots.save(snapshot); } \n" +
            "    ); " +
            "    return 'ok'; \n" +
            "}";

    void runOnInstance(MongoDatabase mongo) {
        MongoCollection<Document> snapshots = mongo.getCollection(SNAPSHOTS);
        FindIterable<Document> allSnapshots = snapshots.find();
        boolean isRequired = Optional.ofNullable(allSnapshots.first())
                .map(snapshot -> (Map) snapshot.get(COMMIT_METADATA_PROPERTY))
                .map(metadata -> metadata.get(COMMIT_ID_PROPERTY))
                .map(id -> id instanceof String)
                .orElse(false);

        if (isRequired) {
            Document update = new Document(EVAL_KEY, UPDATE_FUNCTION);

            Document result = mongo.runCommand(update);
            if (log.isInfoEnabled()) {
                log.info("migration from JaVers 1.1 to 1.2 result: {}", result.toJson());
            }
        }
    }
}
