package org.javers.spring.boot.mongo;

import com.mongodb.client.MongoDatabase;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.repository.mongo.MongoRepository;
import org.javers.spring.auditable.*;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.javers.spring.mongodb.DBRefUnproxyObjectAccessHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import static org.javers.repository.mongo.MongoRepository.mongoRepositoryWithDocumentDBCompatibility;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAspectJAutoProxy
@Import({JaversMongoConfig.class})
public class JaversMongoAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JaversMongoAutoConfiguration.class);

    private final JaversMongoProperties javersMongoProperties;
    private final MongoDatabase mongoDatabase;

    public JaversMongoAutoConfiguration(JaversMongoProperties javersMongoProperties
            , @Qualifier("javersMongoDatabase") MongoDatabase mongoDatabase) {
        this.javersMongoProperties = javersMongoProperties;
        this.mongoDatabase = mongoDatabase;
    }


    @Bean(name = "JaversFromStarter")
    @ConditionalOnMissingBean
    public Javers javers() {
        logger.info("Starting javers-spring-boot-starter-mongo ...");

        MongoRepository javersRepository = createMongoRepository(javersMongoProperties, mongoDatabase);

        return JaversBuilder.javers()
                .registerJaversRepository(javersRepository)
                .withProperties(javersMongoProperties)
                .withObjectAccessHook(new DBRefUnproxyObjectAccessHook())
                .build();
    }

    private MongoRepository createMongoRepository(JaversMongoProperties javersMongoProperties, MongoDatabase mongoDatabase) {
        if (javersMongoProperties.isDocumentDbCompatibilityEnabled()){
            logger.info("enabling Amazon DocumentDB compatibility");
            return mongoRepositoryWithDocumentDBCompatibility(mongoDatabase);
        }
        return new MongoRepository(mongoDatabase);
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

    @Bean
    @ConditionalOnProperty(name = "javers.auditableAspectEnabled", havingValue = "true", matchIfMissing = true)
    public JaversAuditableAspect javersAuditableAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversAuditableAspect(javers, authorProvider, commitPropertiesProvider);
    }

    @Bean
    @ConditionalOnProperty(name = "javers.springDataAuditableRepositoryAspectEnabled", havingValue = "true", matchIfMissing = true)
    public JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect(Javers javers, AuthorProvider authorProvider, CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversSpringDataAuditableRepositoryAspect(javers, authorProvider, commitPropertiesProvider);
    }
}
