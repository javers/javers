package org.javers.spring.mongodb;

import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.repository.mongo.JaversMongoTransactionTemplate;

/**
 * @author bartosz walacik
 */
public final class TransactionalMongoJaversBuilder extends JaversBuilder {

    private final JaversMongoTransactionTemplate javersMongoTransactionTemplate;

    public TransactionalMongoJaversBuilder(JaversMongoTransactionTemplate javersMongoTransactionTemplate) {
        Validate.argumentIsNotNull(javersMongoTransactionTemplate);
        this.javersMongoTransactionTemplate = javersMongoTransactionTemplate;
    }

    @Override
    public Javers build() {
        Javers javersCore = super.assembleJaversInstance();
        if (javersMongoTransactionTemplate instanceof SpringJaversMongoTransactionTemplate) {
            return new JaversTransactionalMongoDecorator(javersCore);
        }
        return javersCore;
    }
}
