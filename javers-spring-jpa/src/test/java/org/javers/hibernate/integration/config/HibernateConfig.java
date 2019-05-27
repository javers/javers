package org.javers.hibernate.integration.config;

import liquibase.integration.spring.SpringLiquibase;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.javers.core.Javers;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.javers.spring.jpa.JpaHibernateConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class HibernateConfig {

    private static final Logger logger = LoggerFactory.getLogger(HibernateConfig.class);
    public static final String H2_URL = "jdbc:h2:mem:test";
    public static final String H2_SECONDARY_URL = "jdbc:h2:mem:test-secondary";
    public static final String TENANT1 = "tenant1";
    public static final String TENANT2 = "tenant2";

    /**
     * Integrates {@link JaversSqlRepository} with Spring {@link JpaTransactionManager}
     */
    @Bean
    public ConnectionProvider jpaConnectionProvider() {
        return new JpaHibernateConnectionProvider();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataSource") DataSource dataSource,
                                                                       @Qualifier("secondaryDataSource") DataSource secondaryDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("org.javers.hibernate.entity", "org.javers.spring.model");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());
        runLiquibase(dataSource);
        runLiquibase(secondaryDataSource);
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }


    @Bean(name = "dataSource")
    @Primary
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl(H2_URL + ";DB_CLOSE_DELAY=-1");
        return dataSource;
    }

    @Bean(name = "secondaryDataSource")
    public DataSource secondaryDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl(H2_SECONDARY_URL + ";DB_CLOSE_DELAY=-1");
        return dataSource;
    }

    @Bean
    public JaversAuditableAspect javersAuditableAspect(Javers javers) {
        return new JaversAuditableAspect(javers, authorProvider(), commitPropertiesProvider());
    }

    @Bean
    public JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect(Javers javers) {
        return new JaversSpringDataAuditableRepositoryAspect(javers, authorProvider(), commitPropertiesProvider());
    }

    /**
     * Required by Repository auto-audit aspect. <br/><br/>
     * <p>
     * Returns mock implementation for testing.
     * <br/>
     * Provide real implementation,
     * when using Spring Security you can use
     * {@link SpringSecurityAuthorProvider}.
     */
    @Bean
    public AuthorProvider authorProvider() {
        return new AuthorProvider() {
            @Override
            public String provide() {
                return "unknown";
            }
        };
    }

    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        final Map<String, String> rv = new HashMap<>();
        rv.put("key", "ok");
        return () -> Collections.unmodifiableMap(rv);
    }

    @Bean
    public MultiTenantConnectionProvider multiTenantConnectionProvider(@Qualifier("dataSource") DataSource dataSource,
                                                                       @Qualifier("secondaryDataSource") DataSource secondaryDataSource) {

        class MultiTenantConnectionProviderImpl extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

            private Map<String, DataSource> datasources = new ConcurrentHashMap<>();

            private MultiTenantConnectionProviderImpl(DataSource dataSource,
                                                      DataSource secondaryDataSource) {
                datasources.put(TENANT1, dataSource);
                datasources.put(TENANT2, secondaryDataSource);
            }

            @Override
            protected DataSource selectAnyDataSource() {
                return datasources.get(TENANT1);
            }

            @Override
            protected DataSource selectDataSource(String tenantId) {
                return datasources.get(tenantId);
            }
        }
        return new MultiTenantConnectionProviderImpl(dataSource, secondaryDataSource);
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "validate");
        properties.setProperty("hibernate.connection.autocommit", "false");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        properties.setProperty("hibernate.current_session_context_class", "thread");
        properties.setProperty("hibernate.enable_lazy_load_no_trans", "true");
        properties.setProperty("hibernate.multiTenancy", "DATABASE");
        properties.setProperty("hibernate.tenant_identifier_resolver", TenantContext.TenantIdentifierResolver.class.getName());
        properties.put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider(dataSource(), secondaryDataSource()));
        return properties;
    }

    private static void runLiquibase(DataSource dataSource) {
        logger.info("run liquibase on {}", dataSource);
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setResourceLoader(new DefaultResourceLoader());
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:changelog.xml");
        liquibase.setDefaultSchema("public");
        liquibase.setDropFirst(false);
        liquibase.setShouldRun(true);
        try {
            liquibase.afterPropertiesSet();
            logger.info("ended");
        } catch (Exception e) {
            throw new IllegalStateException("liquibase fail", e);
        }
    }
}
