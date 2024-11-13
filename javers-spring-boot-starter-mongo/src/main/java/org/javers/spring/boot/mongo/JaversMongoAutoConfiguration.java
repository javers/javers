package org.javers.spring.boot.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoDatabase;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.JaversBuilderPlugin;
import org.javers.repository.mongo.MongoRepository;
import org.javers.spring.RegisterJsonTypeAdaptersPlugin;
import org.javers.spring.aot.JaversSpringNativeHints;
import org.javers.spring.auditable.*;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.javers.spring.mongodb.TransactionalMongoJaversBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoDatabaseUtils;
import org.springframework.data.mongodb.MongoTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.javers.repository.mongo.MongoDialect.DOCUMENT_DB;
import static org.javers.repository.mongo.MongoRepositoryConfigurationBuilder.mongoRepositoryConfiguration;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties({JaversMongoProperties.class})
@Import({RegisterJsonTypeAdaptersPlugin.class})
@ImportRuntimeHints({JaversSpringNativeHints.class})
public class JaversMongoAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JaversMongoAutoConfiguration.class);

    @Autowired
    private JaversMongoProperties javersMongoProperties;

    @Autowired
    private ApplicationContext applicationContext;

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

    @Value("${spring.data.mongodb.database:#{null}}")
    Optional<String> mongoDbName;

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

            return new MongoRepository(
                mongoDatabase,
                mongoRepositoryConfiguration()
                    .withSnapshotCollectionName(javersMongoProperties.getSnapshotCollectionName())
                    .withHeadCollectionName(javersMongoProperties.getHeadCollectionName())
                    .withCacheSize(javersMongoProperties.getSnapshotsCacheSize())
                    .withDialect(DOCUMENT_DB)
                    .withSchemaManagementEnabled(javersMongoProperties.isSchemaManagementEnabled())
                    .build()
            );
        }

        return new MongoRepository(
            mongoDatabase,
            mongoRepositoryConfiguration()
                .withSnapshotCollectionName(javersMongoProperties.getSnapshotCollectionName())
                .withHeadCollectionName(javersMongoProperties.getHeadCollectionName())
                .withCacheSize(javersMongoProperties.getSnapshotsCacheSize())
                .withSchemaManagementEnabled(javersMongoProperties.isSchemaManagementEnabled())
                .build()
        );
    }

    private MongoDatabase initJaversMongoDatabase() {
        if (!javersMongoProperties.isDedicatedMongodbConfigurationEnabled()) {
            return mongoDbName.map(name -> {
                logger.info("connecting Javers to Mongo database '{}' configured in the spring.data.mongodb.database property", name);
                return MongoDatabaseUtils.getDatabase(name, dbFactory);
            }).orElseGet(() -> {
                MongoDatabase mongoDatabase = MongoDatabaseUtils.getDatabase(dbFactory);
                logger.info("connecting Javers to Spring's default Mongo database: '{}'", mongoDatabase.getName());
                return mongoDatabase;
            });
        } else {
            MongoDatabase mongoDatabase = JaversDedicatedMongoFactory
                    .createMongoDatabase(javersMongoProperties, mongoClientSettings);
            logger.info("connecting Javers to dedicated Mongo database '{}' configured in javers.mongodb properties",
                    mongoDatabase.getName());
            return mongoDatabase;
        }
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
