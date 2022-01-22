package org.javers.repository.mongo;

import java.util.Optional;

import static org.javers.repository.mongo.MongoDialect.MONGO_DB;

class MongoRepositoryConfiguration {
    private static final String DEFAULT_SNAPSHOT_COLLECTION_NAME = "jv_snapshots";
    private final static int DEFAULT_CACHE_SIZE = 5000;
    private final static MongoDialect DEFAULT_MONGO_DIALECT = MONGO_DB;

    private final String snapshotCollectionName;
    private final Integer cacheSize;
    private final MongoDialect mongoDialect;

    MongoRepositoryConfiguration(String snapshotCollectionName, Integer cacheSize, MongoDialect mongoDialect) {
        this.snapshotCollectionName = snapshotCollectionName;
        this.cacheSize = cacheSize;
        this.mongoDialect = mongoDialect;
    }

    String getSnapshotCollectionName() {
        return Optional.ofNullable(snapshotCollectionName).orElse(DEFAULT_SNAPSHOT_COLLECTION_NAME);
    }

    Integer getCacheSize() {
        return Optional.ofNullable(cacheSize).orElse(DEFAULT_CACHE_SIZE);
    }

    MongoDialect getMongoDialect() {
        return Optional.ofNullable(mongoDialect).orElse(DEFAULT_MONGO_DIALECT);
    }
}
