package org.javers.spring.boot.sql;

import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.javers.core.Javers;
import org.javers.core.MappingStyle;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.repository.sql.DialectName;
import org.javers.repository.sql.JaversSqlRepository;
import org.javers.repository.sql.SqlRepositoryBuilder;
import org.javers.spring.auditable.*;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.javers.spring.jpa.JpaHibernateConnectionProvider;
import org.javers.spring.jpa.TransactionalJaversBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.persistence.EntityManagerFactory;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(value = {JaversProperties.class, JpaProperties.class})
@AutoConfigureAfter(HibernateJpaAutoConfiguration.class)
public class JaversSqlAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JaversSqlAutoConfiguration.class);

    private final DialectMapper dialectMapper = new DialectMapper();

    @Autowired
    private JaversProperties javersProperties;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public DialectName javersSqlDialectName(){
        SessionFactoryImplementor sessionFactory =
                (SessionFactoryImplementor)entityManagerFactory.unwrap(SessionFactory.class);

        Dialect hibernateDialect = sessionFactory.getDialect();
        logger.info("detected Hibernate dialect: " + hibernateDialect.getClass().getSimpleName());

        return dialectMapper.map(hibernateDialect);
    }

    @Bean(name = "javers")
    @ConditionalOnMissingBean
    public Javers javers(ConnectionProvider connectionProvider) {
        JaversSqlRepository sqlRepository = SqlRepositoryBuilder
                .sqlRepository()
                .withConnectionProvider(connectionProvider)
                .withDialect(javersSqlDialectName())
                .build();

        return TransactionalJaversBuilder
                .javers()
                .registerJaversRepository(sqlRepository)
                .withObjectAccessHook(new HibernateUnproxyObjectAccessHook())
                .withListCompareAlgorithm(ListCompareAlgorithm.valueOf(javersProperties.getAlgorithm().toUpperCase()))
                .withMappingStyle(MappingStyle.valueOf(javersProperties.getMappingStyle().toUpperCase()))
                .withNewObjectsSnapshot(javersProperties.isNewObjectSnapshot())
                .withPrettyPrint(javersProperties.isPrettyPrint())
                .withTypeSafeValues(javersProperties.isTypeSafeValues())
                .scanTypeNames(javersProperties.getPackagesToScan())
                .build();
    }

    @Bean(name = "authorProvider")
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = {"org.springframework.security.core.context.SecurityContextHolder"})
    public AuthorProvider springSecurityAuthorProvider() {
        return new SpringSecurityAuthorProvider();
    }

    @Bean(name = "authorProvider")
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass({"org.springframework.security.core.context.SecurityContextHolder"})
    public AuthorProvider unknownAuthorProvider() {
        return new MockAuthorProvider();
    }

    @Bean(name = "commitPropertiesProvider")
    @ConditionalOnMissingBean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new EmptyPropertiesProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public ConnectionProvider jpaConnectionProvider() {
        return new JpaHibernateConnectionProvider();
    }

    @Bean
    public JaversAuditableAspect javersAuditableAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversAuditableAspect(javers, authorProvider, commitPropertiesProvider());
    }

    @Bean
    public JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversSpringDataAuditableRepositoryAspect(javers, authorProvider, commitPropertiesProvider());
    }
}
