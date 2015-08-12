package org.javers.hibernate.integration.config;

import org.javers.core.Javers;
import org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;
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
public class JaversFieldHibernateProxyConfig extends HibernateConfig{

    //.. JaVers setup ..

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
                .build();
    }
}
