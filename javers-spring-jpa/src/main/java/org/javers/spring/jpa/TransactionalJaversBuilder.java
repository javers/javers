package org.javers.spring.jpa;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.repository.sql.JaversSqlRepository;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author bartosz walacik
 */
public final class TransactionalJaversBuilder extends JaversBuilder {
    private PlatformTransactionManager txManager;

    private TransactionalJaversBuilder() {
    }

    public static TransactionalJaversBuilder javers() {
        return new TransactionalJaversBuilder();
    }

    public TransactionalJaversBuilder withTxManager(PlatformTransactionManager txManager) {
        this.txManager = txManager;
        return this;
    }

    @Override
    public Javers build() {
        if (txManager == null) {
            throw new JaversException(JaversExceptionCode.TRANSACTION_MANAGER_NOT_SET);
        }

        Javers javersCore = super.assembleJaversInstance();

        Javers javersTransactional =
                new JaversTransactionalDecorator(javersCore, getContainerComponent(JaversSqlRepository.class), txManager);

        return javersTransactional;
    }
}
