package org.javers.spring.jpa

import org.javers.core.Javers
import org.javers.hibernate.integration.config.HibernateConfig
import org.javers.repository.sql.ConnectionProvider
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepository
import org.javers.repository.sql.SqlRepositoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Configuration()
@EnableJpaRepositories(basePackages = ["org.javers.hibernate.entity"])
@EnableTransactionManagement()
@EnableAspectJAutoProxy
@Import(HibernateConfig)
class MultiTenancyConfig extends HibernateConfig {

    @PersistenceContext
    private EntityManager entityManager

    @Bean
    JaversSqlRepository sqlRepository(ConnectionProvider jpaConnectionProvider) {
        SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(jpaConnectionProvider)
                .withDialect(DialectName.H2)
                .withSchemaManagementEnabled(false)
                .build()
    }

    @Bean
    Javers javers(JaversSqlRepository sqlRepository, PlatformTransactionManager transactionManager) {
        TransactionalJaversBuilder
                .javers()
                .withTxManager(transactionManager)
                .registerJaversRepository(sqlRepository)
                .build()
    }
}