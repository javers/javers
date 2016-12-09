package org.javers.spring.jpa

import org.javers.core.Javers
import org.javers.hibernate.integration.config.HibernateConfig
import org.javers.hibernate.integration.entity.PersonCrudRepository
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepository
import org.javers.repository.sql.SqlRepositoryBuilder
import org.springframework.context.annotation.*
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration()
@ComponentScan(basePackages = ["org.javers.hibernate"])
@EnableJpaRepositories(basePackages = "org.javers.hibernate")
@EnableTransactionManagement
@EnableAspectJAutoProxy
@Import(HibernateConfig.class)
class CacheEvictSpringConfig extends HibernateConfig {
    @Bean
    JaversSqlRepository sqlRepository(){
        SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(jpaConnectionProvider())
                .withDialect(DialectName.H2)
                .build()
    }

    @Bean
    Javers javers(JaversSqlRepository sqlRepository, PlatformTransactionManager txManager) {
        TransactionalJaversBuilder
                .javers()
                .withTxManager(txManager)
                .registerJaversRepository(sqlRepository)
                .build()
    }

    @Bean
    ErrorThrowingService errorThrowingService(PersonCrudRepository repository) {
        new ErrorThrowingService(repository)
    }
}
