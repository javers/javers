package org.javers.spring.boot.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoDatabase;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.JaversBuilderPlugin;
import org.javers.repository.mongo.MongoRepository;
import org.javers.spring.RegisterJsonTypeAdaptersPlugin;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.EmptyPropertiesProvider;
import org.javers.spring.auditable.MockAuthorProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.javers.spring.mongodb.TransactionalMongoJaversBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoDatabaseUtils;
import org.springframework.data.mongodb.MongoTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.javers.repository.mongo.MongoRepository.mongoRepositoryWithDocumentDBCompatibility;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties({JaversMongoProperties.class})
@Import({RegisterJsonTypeAdaptersPlugin.class})
public class JaversMongoAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JaversMongoAutoConfiguration.class);

    @Autowired
    private JaversMongoProperties javersMongoProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private MongoProperties mongoProperties; //from spring-boot-starter-data-mongodb

    @Autowired
    @Qualifier("javersMongoClientSettings")
    private Optional<MongoClientSettings> mongoClientSettings;

    //dbFactory should be created by the spring-boot-starter-data-mongodb
    @Autowired
    private MongoDatabaseFactory dbFactory;

    @Autowired(required = false)
    private List<JaversBuilderPlugin> plugins = new ArrayList<>();

    @Autowired
    private Optional<MongoTransactionManager> mongoTransactionManager;

    @Bean(name = "JaversFromStarter")
    @ConditionalOnMissingBean
    public Javers javers() {
        logger.info("Starting javers-spring-boot-starter-mongo ...");

        MongoDatabase mongoDatabase = initJaversMongoDatabase();

        MongoRepository javersRepository = createMongoRepository(mongoDatabase);

        JaversBuilder javersBuilder = TransactionalMongoJaversBuilder.javers()
                .registerJaversRepository(javersRepository)
                .withTxManager(mongoTransactionManager.orElse(null))
                .withProperties(javersMongoProperties)
                .withObjectAccessHook(javersMongoProperties.createObjectAccessHookInstance());

        plugins.forEach(plugin -> plugin.beforeAssemble(javersBuilder));

        return javersBuilder.build();
    }

    private MongoRepository createMongoRepository(MongoDatabase mongoDatabase) {
        if (javersMongoProperties.isDocumentDbCompatibilityEnabled()) {
            logger.info("enabling Amazon DocumentDB compatibility");
            return mongoRepositoryWithDocumentDBCompatibility(mongoDatabase, javersMongoProperties.getSnapshotsCacheSize());
        }
        return new MongoRepository(mongoDatabase, javersMongoProperties.getSnapshotsCacheSize());
    }

    private MongoDatabase initJaversMongoDatabase() {
        if (!javersMongoProperties.isDedicatedMongodbConfigurationEnabled()) {
            MongoDatabase mongoDatabase = getDefaultMongoDatabase();
            logger.info("connecting Javers to primary Mongo database '{}' configured in spring.data.mongodb properties",
                        mongoDatabase.getName());
            return mongoDatabase;
        } else {
            MongoDatabase mongoDatabase = JaversDedicatedMongoFactory
                    .createMongoDatabase(javersMongoProperties, mongoClientSettings);
            logger.info("connecting Javers to dedicated Mongo database '{}' configured in javers.mongodb properties",
                    mongoDatabase.getName());
            return mongoDatabase;
        }
    }

    private MongoDatabase getDefaultMongoDatabase() {
        return MongoDatabaseUtils.getDatabase(dbFactory);
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
    public JaversAuditableAspect javersAuditableAspect(
            Javers javers,
            AuthorProvider authorProvider,
            CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversAuditableAspect(javers, authorProvider, commitPropertiesProvider);
    }

    @Bean
    @ConditionalOnProperty(name = "javers.springDataAuditableRepositoryAspectEnabled", havingValue = "true", matchIfMissing = true)
    public JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect(
            Javers javers,
            AuthorProvider authorProvider,
            CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversSpringDataAuditableRepositoryAspect(javers, authorProvider, commitPropertiesProvider);
    }
}
