package org.javers.repository.mongo;

public class CollectionNameProvider {

  private static final String DEFAULT_SNAPSHOT_COLLECTION_NAME = "jv_snapshots";
  private final MongoRepositoryConfiguration configuration;

  public CollectionNameProvider(MongoRepositoryConfiguration configuration) {
    this.configuration = configuration;
  }

  String getSnapshotCollectionName() {
    return configuration.getSnapshotCollectionName().orElse(DEFAULT_SNAPSHOT_COLLECTION_NAME);
  }
}
