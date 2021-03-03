package org.javers.spring.boot.sql;

import javax.persistence.EntityManagerFactory;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.javers.core.Javers;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.EmptyPropertiesProvider;
import org.javers.spring.auditable.MockAuthorProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdatajpa.JaversSpringDataJpaAuditableRepositoryAspect;
import org.javers.spring.jpa.JpaHibernateConnectionProvider;
import org.javers.spring.jpa.TransactionalJaversBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(value = {JaversSqlProperties.class, JpaProperties.class})
@AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
public class JaversSqlAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JaversSqlAutoConfiguration.class);

    private final DialectMapper dialectMapper = new DialectMapper();

    @Autowired
    private JaversSqlProperties javersSqlProperties;

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
                .withGlobalIdPKColumnName(javersSqlProperties.getSqlGlobalIdPKColumnName())
                .withGlobalIdLocalIdColumnName(javersSqlProperties.getSqlGlobalIdLocalIdColumnName())
                .withGlobalIdFragmentColumnName(javersSqlProperties.getSqlGlobalIdFragmentColumnName())
                .withGlobalIdTypeNameColumnName(javersSqlProperties.getSqlGlobalIdTypeNameColumnName())
                .withGlobalIdOwnerIDFKColumnName(javersSqlProperties.getSqlGlobalIdOwnerIDFKColumnName())
                .withCommitPKColumnName(javersSqlProperties.getSqlCommitPKColumnName())
                .withCommitAuthorColumnName(javersSqlProperties.getSqlCommitAuthorColumnName())
                .withCommitCommitDateColumnName(javersSqlProperties.getSqlCommitCommitDateColumnName())
                .withCommitCommitDateInstantColumnName(javersSqlProperties.getSqlCommitCommitDateInstantColumnName())
                .withCommitCommitIdColumName(javersSqlProperties.getSqlCommitCommitIdColumName())
                .withCommitPropertyCommitFKColumnName(javersSqlProperties.getSqlCommitPropertyCommitFKColumnName())
                .withCommitPropertyNameColumnName(javersSqlProperties.getSqlCommitPropertyNameColumnName())
                .withCommitPropertyValueColumnName(javersSqlProperties.getSqlCommitPropertyValueColumnName())
                .withSnapshotPKColumnName(javersSqlProperties.getSqlSnapshotPKColumnName())
                .withSnapshotCommitFKColumnName(javersSqlProperties.getSqlSnapshotCommitFKColumnName())
                .withSnapshotGlobalIdFKColumnName(javersSqlProperties.getSqlSnapshotGlobalIdFKColumnName())
                .withSnapshotTypeColumnName(javersSqlProperties.getSqlSnapshotTypeColumnName())
                .withSnapshotVersionColumnName(javersSqlProperties.getSqlSnapshotVersionColumnName())
                .withSnapshotStateColumnName(javersSqlProperties.getSqlSnapshotStateColumnName())
                .withSnapshotChangedColumnName(javersSqlProperties.getSqlSnapshotChangedColumnName())
                .withSnapshotManagedTypeColumnName(javersSqlProperties.getSqlSnapshotManagedTypeColumnName())
                .withPrimaryKeyIndicator(javersSqlProperties.getSqlPrimaryKeyIndicator())
                .withForeignKeyIndicator(javersSqlProperties.getSqlForeignKeyIndicator())
                .withSequenceIndicator(javersSqlProperties.getSqlSequenceIndicator())
                .withIndexIndicator(javersSqlProperties.getSqlIndexIndicator())
                .withIsSuffix(javersSqlProperties.getSqlIsSuffix())
                .build();
    }

    @Bean(name = "JaversFromStarter")
    @ConditionalOnMissingBean
    public Javers javers(JaversSqlRepository sqlRepository, PlatformTransactionManager transactionManager) {
        return TransactionalJaversBuilder
                .javers()
                .withTxManager(transactionManager)
                .registerJaversRepository(sqlRepository)
                .withObjectAccessHook(javersSqlProperties.createObjectAccessHookInstance())
                .withProperties(javersSqlProperties)
                .build();
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
    public ConnectionProvider jpaConnectionProvider() {
        return new JpaHibernateConnectionProvider();
    }

    @Bean
    @ConditionalOnProperty(name = "javers.auditableAspectEnabled", havingValue = "true", matchIfMissing = true)
    public JaversAuditableAspect javersAuditableAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversAuditableAspect(javers, authorProvider, commitPropertiesProvider);
    }

    @Bean
    @ConditionalOnProperty(name = "javers.springDataAuditableRepositoryAspectEnabled", havingValue = "true", matchIfMissing = true)
    public JaversSpringDataJpaAuditableRepositoryAspect javersSpringDataAuditableAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversSpringDataJpaAuditableRepositoryAspect(javers, authorProvider, commitPropertiesProvider);
    }
}
