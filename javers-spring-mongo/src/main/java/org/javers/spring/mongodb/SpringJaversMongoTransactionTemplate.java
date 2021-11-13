package org.javers.spring.mongodb;

import com.mongodb.client.ClientSession;
import org.javers.common.validation.Validate;
import org.javers.repository.mongo.JaversMongoTransactionTemplate;
import org.springframework.data.mongodb.ClientSessionExtractor;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

public class SpringJaversMongoTransactionTemplate implements JaversMongoTransactionTemplate {
    private final TransactionTemplate springTransactionTemplate;

    public SpringJaversMongoTransactionTemplate(MongoTransactionManager mongoTransactionManager) {
        Validate.argumentsAreNotNull(mongoTransactionManager);
        this.springTransactionTemplate = new TransactionTemplate(mongoTransactionManager);
    }

    @Override
    public void execute(TransactionCallback callback) {
        springTransactionTemplate.execute(status -> {
            ClientSession session = ClientSessionExtractor.getFrom((DefaultTransactionStatus)status);
            callback.doInTransaction(Optional.of(session));
            return null;
        });
    }
}
