package org.javers.hibernate.integration

import org.javers.core.Javers
import org.javers.hibernate.integration.config.HibernateConfig
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepository
import org.javers.repository.sql.SqlRepositoryBuilder
import org.javers.spring.jpa.JpaHibernateConnectionProvider
import org.javers.spring.jpa.TransactionalJaversBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableJpaRepositories(basePackages = ["org.javers.hibernate.entity"])
@Import(HibernateConfig)
class JaversFieldHibernateProxyConfig {

    //.. JaVers setup ..

    /**
     * Creates JaVers instance with {@link JaversSqlRepository}
     */
    @Bean
    Javers javers(JpaHibernateConnectionProvider jpaHibernateConnectionProvider,
                  PlatformTransactionManager txManager) {
        JaversSqlRepository sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(jpaHibernateConnectionProvider)
                .withDialect(DialectName.H2)
                .build()

        return TransactionalJaversBuilder
                .javers()
                .withTxManager(txManager)
                .registerJaversRepository(sqlRepository)
                .withObjectAccessHook(new HibernateUnproxyObjectAccessHook())
                .build()
    }
}
