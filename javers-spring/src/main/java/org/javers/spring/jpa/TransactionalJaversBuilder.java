package org.javers.spring.jpa;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.repository.sql.JaversSqlRepository;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author bartosz walacik
 */
public final class TransactionalJaversBuilder extends JaversBuilder {
    private static final Logger logger = getLogger(TransactionalJaversBuilder.class);

    private TransactionalJaversBuilder() {
    }

    public static TransactionalJaversBuilder javers() {
        return new TransactionalJaversBuilder();
    }

    @Override
    public Javers build() {
        Javers javersCore = super.assembleJaversInstance();

        Javers javersTransactional =
                new JaversTransactionalDecorator(javersCore, getContainerComponent(JaversSqlRepository.class));

        logger.info("JaVers transactional decorator is ready");
        return javersTransactional;
    }
}
