package org.javers.hibernate.integration.config;

import org.javers.core.Javers;
import org.javers.core.MappingStyle;
import org.javers.hibernate.HibernateUnproxyObjectAccessHook;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;
import org.javers.spring.annotation.JaversAuditable;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.aspect.JaversAuditableRepositoryAspect;
import org.javers.spring.jpa.JpaHibernateConnectionProvider;
import org.javers.spring.jpa.TransactionalJaversBuilder;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author bartosz walacik
 */
@Configuration
@ComponentScan(basePackages = "org.javers.hibernate")
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = "org.javers.hibernate")
@Import(HibernateConfig.class)
public class JaversBeanHibernateProxyConfig {

    /**
     * Creates JaVers instance with {@link JaversSqlRepository}
     */
    @Bean
    public Javers javers(JpaHibernateConnectionProvider jpaHibernateConnectionProvider) {

        JaversSqlRepository sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(jpaHibernateConnectionProvider)
                .withDialect(DialectName.H2)
                .build();

        return TransactionalJaversBuilder
                .javers()
                .registerJaversRepository(sqlRepository)
                .withObjectAccessHook(new HibernateUnproxyObjectAccessHook())
                .withMappingStyle(MappingStyle.BEAN)
                .build();
    }

}
