package org.javers.spring.boot.sql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.JaversBuilderPlugin;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;
import org.javers.spring.RegisterJsonTypeAdaptersPlugin;
import org.javers.spring.aot.JaversSpringNativeHints;
import org.javers.spring.auditable.*;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdatajpa.JaversSpringDataJpaAuditableRepositoryAspect;
import org.javers.spring.boot.aot.JaversSqlNativeHints;
import org.javers.spring.jpa.JpaHibernateConnectionProvider;
import org.javers.spring.jpa.TransactionalJpaJaversBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.context.annotation.*;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(value = {JaversSqlProperties.class, JpaProperties.class})
@AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
@Import({RegisterJsonTypeAdaptersPlugin.class})
@ImportRuntimeHints({JaversSpringNativeHints.class, JaversSqlNativeHints.class})
public class JaversSqlAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JaversSqlAutoConfiguration.class);

    private final DialectMapper dialectMapper = new DialectMapper();

    @Autowired
    private JaversSqlProperties javersSqlProperties;

    @Autowired(required = false)
    private List<JaversBuilderPlugin> plugins = new ArrayList<>();

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public DialectName javersSqlDialectName() {
        SessionFactoryImplementor sessionFactory =
                entityManagerFactory.unwrap(SessionFactoryImplementor.class);

        Dialect hibernateDialect = sessionFactory.getJdbcServices().getDialect();
        logger.info("detected Hibernate dialect: " + hibernateDialect.getClass().getSimpleName());

        return dialectMapper.map(hibernateDialect);
    }

    @Bean(name = "JaversSqlRepositoryFromStarter")
    @ConditionalOnMissingBean
    public JaversSqlRepository javersSqlRepository(ConnectionProvider connectionProvider) {
        return SqlRepositoryBuilder
                .sqlRepository()
                .withSchema(javersSqlProperties.getSqlSchema())
                .withConnectionProvider(connectionProvider)
                .withDialect(javersSqlDialectName())
                .withSchemaManagementEnabled(javersSqlProperties.isSqlSchemaManagementEnabled())
                .withGlobalIdCacheDisabled(javersSqlProperties.isSqlGlobalIdCacheDisabled())
                .withGlobalIdTableName(javersSqlProperties.getSqlGlobalIdTableName())
                .withCommitTableName(javersSqlProperties.getSqlCommitTableName())
                .withSnapshotTableName(javersSqlProperties.getSqlSnapshotTableName())
                .withCommitPropertyTableName(javersSqlProperties.getSqlCommitPropertyTableName())
                .withGlobalIdSequenceName(javersSqlProperties.getSqlGlobalIdSequenceName())
                .withCommitSequenceName(javersSqlProperties.getSqlCommitSequenceName())
                .withSnapshotSequenceName(javersSqlProperties.getSqlSnapshotSequenceName())
                .build();
    }

    @Bean(name = "JaversFromStarter")
    @ConditionalOnMissingBean
    public Javers javers(JaversSqlRepository sqlRepository,
                         PlatformTransactionManager transactionManager) {
        JaversBuilder javersBuilder = TransactionalJpaJaversBuilder
                .javers()
                .withTxManager(transactionManager)
                .registerJaversRepository(sqlRepository)
                .withObjectAccessHook(javersSqlProperties.createObjectAccessHookInstance())
                .withProperties(javersSqlProperties);

        plugins.forEach(plugin -> plugin.beforeAssemble(javersBuilder));

        return javersBuilder.build();
    }

    @Bean(name = "SpringSecurityAuthorProvider")
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = {"org.springframework.security.core.context.SecurityContextHolder"})
    public AuthorProvider springSecurityAuthorProvider() {
        return new SpringSecurityAuthorProvider();
    }

    @Bean(name = "MockAuthorProvider")
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass({"org.springframework.security.core.context.SecurityContextHolder"})
    public AuthorProvider unknownAuthorProvider() {
        return new MockAuthorProvider();
    }

    @Bean(name = "EmptyPropertiesProvider")
    @ConditionalOnMissingBean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new EmptyPropertiesProvider();
    }

    @Bean(name = "JpaHibernateConnectionProvider")
    @ConditionalOnMissingBean
    public ConnectionProvider jpaConnectionProvider(EntityManager entityManager) {
        return new JpaHibernateConnectionProvider(entityManager);
    }

    @Bean
    @ConditionalOnProperty(name = "javers.auditableAspectEnabled", havingValue = "true", matchIfMissing = true)
    public JaversAuditableAspect javersAuditableAspect(Javers javers,
                                                       AuthorProvider authorProvider,
                                                       CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversAuditableAspect(javers, authorProvider, commitPropertiesProvider);
    }

    @Bean
    @ConditionalOnProperty(name = "javers.springDataAuditableRepositoryAspectEnabled", havingValue = "true", matchIfMissing = true)
    public JaversSpringDataJpaAuditableRepositoryAspect javersSpringDataAuditableAspect(
            Javers javers,
            AuthorProvider authorProvider,
            CommitPropertiesProvider commitPropertiesProvider
    ) {
        return new JaversSpringDataJpaAuditableRepositoryAspect(javers, authorProvider, commitPropertiesProvider);
    }
}
