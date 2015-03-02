package org.javers.spring.jpa.connectionprovider

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.repository.sql.ConnectionProvider
import org.javers.repository.sql.DialectName
import org.javers.repository.sql.JaversSqlRepository
import org.javers.repository.sql.SqlRepositoryBuilder
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.aspect.JaversAuditableRepositoryAspect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

/**
 * @author bartosz walacik
 */
@Configuration
@ComponentScan
@EnableTransactionManagement
@EnableAspectJAutoProxy
class JpaHibernateConnectionProviderApplicationConfig {

    //JaVers setup
    @Bean
    public SqlRepositoryBuilder javersSqlRepositoryBuilder(){
        return SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(jpaConnectionProvider())
                .withDialect(DialectName.H2)
    }


    @Bean
    public JaversSqlRepository javersSqlRepository() {
        return javersSqlRepositoryBuilder().build()
    }

    @Bean
    public Javers javers() {
        JaversSqlRepository sqlRepository = javersSqlRepositoryBuilder().build()
        return JaversBuilder.javers().registerJaversRepository(sqlRepository).build()
    }

    @Bean
    public AuthorProvider authorProvider() {
        return { "author" } as AuthorProvider
    }

    @Bean
    public JaversAuditableRepositoryAspect javersAuditableRepositoryAspect() {
        return new JaversAuditableRepositoryAspect(javers(), authorProvider())
    }

    @Bean
    public ConnectionProvider jpaConnectionProvider() {
        return new JpaHibernateConnectionProvider()
    }
    //EOF JaVers setup

    //test JPA repository
    @Bean
    public DummyJpaRepository dummyJpaRepository(){
        return new DummyJpaRepository();
    }

    //Spring-JPA-Hibernate setup
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("org.javers.spring.model");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        return dataSource;
    }

    Properties additionalProperties() {
        Properties properties = new Properties()
        properties.setProperty("hibernate.hbm2ddl.auto", "create")
        properties.setProperty("hibernate.show_sql", "true")
        properties.setProperty("hibernate.connection.autocommit", "false")
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
        return properties;
    }
    //EOF Spring-JPA-Hibernate setup
}
