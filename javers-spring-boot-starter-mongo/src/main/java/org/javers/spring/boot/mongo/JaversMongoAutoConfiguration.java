package org.javers.spring.boot.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.MappingStyle;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.mongo.MongoRepository;
import org.javers.spring.auditable.*;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties({JaversProperties.class})
public class JaversMongoAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JaversMongoAutoConfiguration.class);

    @Autowired
    private JaversProperties javersProperties;

    @Autowired
    private MongoClient mongoClient; //from spring-boot-starter-data-mongodb

    @Autowired
    private MongoProperties mongoProperties; //from spring-boot-starter-data-mongodb

    @Bean(name = "javers")
    @ConditionalOnMissingBean
    public Javers javers() {
        logger.info("Starting javers-spring-boot-starter-mongo ...");

        MongoDatabase mongoDatabase = mongoClient.getDatabase( mongoProperties.getMongoClientDatabase() );

        logger.info("connecting to database: {}", mongoProperties.getMongoClientDatabase());

        JaversRepository javersRepository = new MongoRepository(mongoDatabase);

        return JaversBuilder.javers()
                .withListCompareAlgorithm(ListCompareAlgorithm.valueOf(javersProperties.getAlgorithm().toUpperCase()))
                .withMappingStyle(MappingStyle.valueOf(javersProperties.getMappingStyle().toUpperCase()))
                .withNewObjectsSnapshot(javersProperties.isNewObjectSnapshot())
                .withPrettyPrint(javersProperties.isPrettyPrint())
                .withTypeSafeValues(javersProperties.isTypeSafeValues())
                .registerJaversRepository(javersRepository)
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
    public JaversAuditableAspect javersAuditableAspect(Javers javers, AuthorProvider authorProvider) {
        return new JaversAuditableAspect(javers, authorProvider, commitPropertiesProvider());
    }

    @Bean
    public JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect(Javers javers, AuthorProvider authorProvider) {
        return new JaversSpringDataAuditableRepositoryAspect(javers, authorProvider, commitPropertiesProvider());
    }
}
