package org.javers.spring.mongodb;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.mongo.MongoRepository;
import org.springframework.data.mongodb.MongoTransactionManager;

import static org.javers.common.validation.Validate.argumentCheck;

/**
 * Creates transactional Javers instance. <br/>
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
 * @since 6.5
 */
public final class TransactionalMongoJaversBuilder extends JaversBuilder {

    private MongoRepository mongoRepository;

    private MongoTransactionManager txManager;

    private TransactionalMongoJaversBuilder() {
    }

    public static TransactionalMongoJaversBuilder javers() {
        return new TransactionalMongoJaversBuilder();
    }

    /**
     * @param txManager nullable
     */
    public TransactionalMongoJaversBuilder withTxManager(MongoTransactionManager txManager) {
        this.txManager = txManager;
        return this;
    }

    @Override
    public TransactionalMongoJaversBuilder registerJaversRepository(JaversRepository mongoRepository) {
        argumentCheck(mongoRepository instanceof MongoRepository, "MongoRepository expected");
        this.mongoRepository = (MongoRepository) mongoRepository;
        return this;
    }

    @Override
    public Javers build() {

        if (txManager != null) {
            logger.info("creating Javers' MongoRepository with multi-document transactions support");
            super.registerJaversRepository(new TransactionalMongoRepository(mongoRepository, txManager));
        } else {
            logger.info("creating Javers' MongoRepository without multi-document transactions support, " +
                    "as there is no MongoTransactionManager provided");
            super.registerJaversRepository(mongoRepository);

        }
        Javers javersCore = super.assembleJaversInstance();
        return javersCore;
    }
}
