package org.javers.repository.mongo;

import static org.javers.common.string.Strings.isNonEmpty;

import com.mongodb.client.MongoDatabase;
import org.javers.core.AbstractContainerBuilder;
import org.javers.repository.mongo.pico.JaversMongoModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoRepositoryBuilder extends AbstractContainerBuilder {

  private static final Logger logger = LoggerFactory.getLogger(MongoRepositoryBuilder.class);
  private String snapshotCollectionName;
  private Integer cacheSize;
  private MongoDialect dialect;
  private MongoDatabase mongoDatabase;

  public MongoRepositoryBuilder() {
  }

  public static MongoRepositoryBuilder mongoRepository() {
    return new MongoRepositoryBuilder();
  }

  public MongoRepositoryBuilder withSnapshotCollectionName(String snapshotCollectionName) {
    if (isNonEmpty(snapshotCollectionName)) {
      this.snapshotCollectionName = snapshotCollectionName;
    }
    return this;
  }

  public MongoRepositoryBuilder withCacheSize(int cacheSize) {
    this.cacheSize = cacheSize;
    return this;
  }

  public MongoRepositoryBuilder withDialect(MongoDialect dialect) {
    this.dialect = dialect;
    return this;
  }

  public MongoRepositoryBuilder withMongoDatabase(MongoDatabase mongoDatabase) {
    this.mongoDatabase = mongoDatabase;
    return this;
  }

  public MongoRepository build() {
    logger.info("starting MongoRepository...");
    logger.info("  snapshotCollection name:              {}", snapshotCollectionName);
    logger.info("  cacheSize              :              {}", cacheSize);
    logger.info("  dialect                :              {}", dialect);
    bootContainer();

    MongoRepositoryConfiguration config = new MongoRepositoryConfiguration(snapshotCollectionName,
        cacheSize, dialect);
    addComponent(config);

    addComponent(mongoDatabase);

    addModule(new JaversMongoModule());

    return getContainerComponent(MongoRepository.class);
  }

  /**
   * For testing only
   */
  @Override
  protected <T> T getContainerComponent(Class<T> ofClass) {
    return super.getContainerComponent(ofClass);
  }
}
