package org.javers.spring.jpa;

import jakarta.transaction.Transactional;
import org.javers.common.validation.Validate;
import org.javers.core.Javers;
import org.javers.core.commit.Commit;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.spring.transactions.JaversTransactionalDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.*;

import java.util.Map;

/**
 * Transactional wrapper for core JaVers instance.
 * Provides integration with Spring JPA TransactionManager
 *
 * @author bartosz walacik
 */
public class JaversTransactionalJpaDecorator extends JaversTransactionalDecorator implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JaversTransactionalJpaDecorator.class);

    private final JaversSqlRepository javersSqlRepository;

    private final PlatformTransactionManager txManager;

    JaversTransactionalJpaDecorator(Javers delegate, JaversSqlRepository javersSqlRepository, PlatformTransactionManager txManager) {
        super(delegate);
        Validate.argumentsAreNotNull(javersSqlRepository, txManager);
        this.javersSqlRepository = javersSqlRepository;
        this.txManager = txManager;
    }


    @Override
    @Transactional
    public Commit commit(String author, Object currentVersion) {
        registerRollbackListener();
        return super.commit(author, currentVersion);
    }

    @Override
    @Transactional
    public Commit commit(String author, Object currentVersion, Map<String, String> commitProperties) {
        registerRollbackListener();
        return super.commit(author, currentVersion, commitProperties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
       ensureSchema();
    }

    private void ensureSchema() {
        if (javersSqlRepository.getConfiguration().isSchemaManagementEnabled()) {
            TransactionTemplate tmpl = new TransactionTemplate(txManager);
            tmpl.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    javersSqlRepository.ensureSchema();
                }
            });
        }
    }

    private void registerRollbackListener() {
        if (javersSqlRepository.getConfiguration().isGlobalIdCacheDisabled()) {
            return;
        }
        if(TransactionSynchronizationManager.isSynchronizationActive() &&
           TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
                @Override
                public void afterCompletion(int status) {
                    if (TransactionSynchronization.STATUS_ROLLED_BACK == status) {
                        logger.info("evicting javersSqlRepository local cache due to transaction rollback");
                        javersSqlRepository.evictCache();
                    }
                }
            });
        }
    }
}
