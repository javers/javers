package org.javers.repository.mongo;

import static org.javers.common.string.Strings.isNonEmpty;

public class MongoRepositoryConfigurationBuilder {

    private String snapshotCollectionName;
    private Integer cacheSize;
    private MongoDialect dialect;

    public static MongoRepositoryConfigurationBuilder mongoRepositoryConfiguration() {
        return new MongoRepositoryConfigurationBuilder();
    }

    MongoRepositoryConfigurationBuilder() {
    }


    public MongoRepositoryConfigurationBuilder withSnapshotCollectionName(String snapshotCollectionName) {
        if (isNonEmpty(snapshotCollectionName)) {
            this.snapshotCollectionName = snapshotCollectionName;
        }
        return this;
    }

    public MongoRepositoryConfigurationBuilder withCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
        return this;
    }

    public MongoRepositoryConfigurationBuilder withDialect(MongoDialect dialect) {
        this.dialect = dialect;
        return this;
    }

    public MongoRepositoryConfiguration build() {
        return new MongoRepositoryConfiguration(snapshotCollectionName, cacheSize, dialect);
    }
}
