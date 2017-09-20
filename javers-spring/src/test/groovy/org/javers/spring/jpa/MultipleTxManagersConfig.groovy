package org.javers.spring.jpa

import org.javers.core.Javers
import org.javers.hibernate.integration.config.HibernateConfig
import org.javers.hibernate.integration.config.HibernateConfig
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepository
import org.javers.repository.sql.SqlRepositoryBuilder
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.TransactionManagementConfigurer

import javax.persistence.EntityManagerFactory

@Configuration()
@EnableJpaRepositories(basePackages = ["org.javers.hibernate.entity"])
@EnableTransactionManagement()
@EnableAspectJAutoProxy
@Import(HibernateConfig)
class MultipleTxManagersConfig extends HibernateConfig implements TransactionManagementConfigurer {


    @Override
    PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager()
    }

    @Bean
    PlatformTransactionManager secondTransactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    JaversSqlRepository sqlRepository(){
        SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(jpaConnectionProvider())
                .withDialect(DialectName.H2)
                .build()
    }

    @Bean
    Javers javers(JaversSqlRepository sqlRepository,
                  @Qualifier("transactionManager") PlatformTransactionManager transactionManager) {
        TransactionalJaversBuilder
                .javers()
                .withTxManager(transactionManager)
                .registerJaversRepository(sqlRepository)
                .build()
    }
}