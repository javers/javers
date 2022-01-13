package org.javers.repository.mongo;

import java.util.Optional;

public class MongoRepositoryConfiguration {

  private final String snapshotCollectionName;
  private final Integer cacheSize;
  private final MongoDialect mongoDialect;

  public MongoRepositoryConfiguration(Integer cacheSize, MongoDialect mongoDialect) {
    this(null, cacheSize, mongoDialect);
  }

  public MongoRepositoryConfiguration(String snapshotCollectionName, Integer cacheSize, MongoDialect mongoDialect) {
    this.snapshotCollectionName = snapshotCollectionName;
    this.cacheSize = cacheSize;
    this.mongoDialect = mongoDialect;
  }

  public Optional<String> getSnapshotCollectionName() {
    return Optional.ofNullable(snapshotCollectionName);
  }

  public Optional<Integer> getCacheSize() {
    return Optional.ofNullable(cacheSize);
  }

  public Optional<MongoDialect> getMongoDialect() {
    return Optional.ofNullable(mongoDialect);
  }
}
