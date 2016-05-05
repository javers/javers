package org.javers.spring.boot.mongo;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.MappingStyle;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.mongo.MongoRepository;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;
import org.javers.spring.auditable.aspect.JaversAuditableRepositoryAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

    @Bean
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
                .build();
    }

    @Bean(name = "authorProvider")
    @ConditionalOnMissingBean
    public AuthorProvider springSecurityAuthorProvider() {
        return new SpringSecurityAuthorProvider();
    }

    @Bean
    public JaversAuditableRepositoryAspect javersAuditableRepositoryAspect(Javers javers, AuthorProvider authorProvider) {
        return new JaversAuditableRepositoryAspect(javers, authorProvider);
    }
}
