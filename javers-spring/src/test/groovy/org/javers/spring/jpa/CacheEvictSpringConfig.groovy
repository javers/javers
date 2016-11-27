package org.javers.spring.jpa

import org.javers.core.Javers
import org.javers.hibernate.integration.config.HibernateConfig
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepository
import org.javers.repository.sql.SqlRepositoryBuilder
import org.springframework.context.annotation.*
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration()
@ComponentScan(basePackages = ["org.javers.hibernate", "org.javers.spring.jpa"])
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
    Javers javers(JaversSqlRepository sqlRepository) {
        TransactionalJaversBuilder
                .javers()
                .registerJaversRepository(sqlRepository)
                .build();
    }
}
