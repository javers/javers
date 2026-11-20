package org.javers.spring.mongodb;

import com.mongodb.client.MongoDatabase;
import org.javers.common.validation.Validate;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.mongo.MongoRepository;
import org.javers.repository.mongo.MongoRepositoryConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import java.util.Optional;
import static org.javers.repository.mongo.MongoRepositoryConfigurationBuilder.mongoRepositoryConfiguration;

/**
 * Creates transactional version of {@link MongoRepository} if {@link MongoTransactionManager} is provided.
 * <br/><br/>
 *
 * Allows Javers' {@link MongoRepository} to participate in application's transactions
 * managed by {@link MongoTransactionManager}.
 * <br/><br/>
 *
 * Note that multi-document ACID transactions was introduced in MongoDB 4.0 and
 * Spring Data MongoDB 2.1.
 * <br/><br/>
 *
 * See <a href="https://www.baeldung.com/spring-data-mongodb-transactions">https://www.baeldung.com/spring-data-mongodb-transactions</a>,
 * <a href="https://www.mongodb.com/blog/post/mongodb-multi-document-acid-transactions-general-availability">https://www.mongodb.com/blog/post/mongodb-multi-document-acid-transactions-general-availability</a>
 *
 * @since 7.12
 */
public final class TransactionalMongoRepositoryBuilder {
    private static final Logger logger = LoggerFactory.getLogger(TransactionalMongoRepositoryBuilder.class);

    private MongoRepositoryConfiguration mongoRepositoryConfiguration;
    private MongoDatabase mongo;
    private MongoTransactionManager txManager;

    private TransactionalMongoRepositoryBuilder(MongoDatabase mongoDatabase) {
        Validate.argumentsAreNotNull(mongoDatabase);
        this.mongo = mongoDatabase;
    }

    public static TransactionalMongoRepositoryBuilder transactionalMongoRepository(MongoDatabase mongoDatabase) {
        return new TransactionalMongoRepositoryBuilder(mongoDatabase);
    }

    /**
     * @param txManager nullable
     */
    public TransactionalMongoRepositoryBuilder withTxManager(MongoTransactionManager txManager) {
        this.txManager = txManager;
        return this;
    }

    public TransactionalMongoRepositoryBuilder withTxManager(Optional<MongoTransactionManager> txManager) {
        Validate.argumentsAreNotNull(txManager);
        return withTxManager(txManager.orElse(null));
    }

    public TransactionalMongoRepositoryBuilder withMongoRepositoryConfiguration(MongoRepositoryConfiguration mongoRepositoryConfiguration) {
        this.mongoRepositoryConfiguration = mongoRepositoryConfiguration;
        return this;
    }

    public JaversRepository build() {

        if (mongoRepositoryConfiguration == null) {
            mongoRepositoryConfiguration = mongoRepositoryConfiguration().build();
        }

        if (txManager != null) {
            logger.info("creating Javers' MongoRepository with transactions support");

            return new TransactionalMongoRepository(mongo, mongoRepositoryConfiguration, txManager);
        } else {
            logger.info("creating Javers' MongoRepository without transactions support, " +
                    "as there is no MongoTransactionManager provided");
            return new MongoRepository(mongo, mongoRepositoryConfiguration);

        }
    }
}
