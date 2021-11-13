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
public final class TransactionalJpaJaversBuilder extends JaversBuilder {
    private PlatformTransactionManager txManager;

    private TransactionalJpaJaversBuilder() {
    }

    public static TransactionalJpaJaversBuilder javers() {
        return new TransactionalJpaJaversBuilder();
    }

    public TransactionalJpaJaversBuilder withTxManager(PlatformTransactionManager txManager) {
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
                new JaversTransactionalJpaDecorator(javersCore, getContainerComponent(JaversSqlRepository.class), txManager);

        return javersTransactional;
    }
}
