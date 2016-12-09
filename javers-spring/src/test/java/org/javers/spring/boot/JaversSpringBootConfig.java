package org.javers.spring.boot;

import org.javers.core.Javers;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.javers.spring.jpa.JpaHibernateConnectionProvider;
import org.javers.spring.jpa.TransactionalJaversBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.transaction.Transactional;

/**
 * @author bartosz.walacik
 */
@Configuration
public class JaversSpringBootConfig {

    @Bean
    public Javers javers(PlatformTransactionManager txManager) {
        JaversSqlRepository sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(jpaConnectionProvider())
                .withDialect(DialectName.H2)
                .build();

        return TransactionalJaversBuilder
                .javers()
                .withTxManager(txManager)
                .registerJaversRepository(sqlRepository)
                .build();
    }

    @Bean
    public JaversAuditableAspect javersAuditableAspect(Javers javers) {
        return new JaversAuditableAspect(javers, authorProvider());
    }

    @Bean
    public JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect(Javers javers) {
        return new JaversSpringDataAuditableRepositoryAspect(javers, authorProvider());
    }

    @Bean
    public ConnectionProvider jpaConnectionProvider() {
        return new JpaHibernateConnectionProvider();
    }

    @Bean
    public AuthorProvider authorProvider() {
        return new AuthorProvider() {
            public String provide() {
                return "unknown";
            }
        };
    }

}
