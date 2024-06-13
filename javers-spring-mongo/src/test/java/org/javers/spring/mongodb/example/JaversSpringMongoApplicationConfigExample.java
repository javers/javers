package org.javers.spring.mongodb.example;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.javers.common.collections.Maps;
import org.javers.core.Javers;
import org.javers.repository.mongo.MongoRepository;
import org.javers.spring.annotation.JaversAuditable;
import org.javers.spring.annotation.JaversAuditableAsync;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.auditable.*;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.JaversAuditableAspectAsync;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.javers.spring.mongodb.TransactionalMongoJaversBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
@ComponentScan(basePackages = "org.javers.spring.mongodb.example")
@EnableMongoRepositories({"org.javers.spring.mongodb.example"})
@EnableAspectJAutoProxy
public class JaversSpringMongoApplicationConfigExample {
    private static final String DATABASE_NAME = "mydatabase";

    @Autowired
    Optional<MongoTransactionManager> mongoTransactionManager;

    /**
     * Creates JaVers instance backed by {@link MongoRepository}
     * <br/><br/>
     *
     * If you are using multi-document ACID transactions
     * introduced in MongoDB 4.0 -- you can configure
     * Javers' to participate in your application's transactions
     * managed by MongoTransactionManager.
     */
    @Bean
    public Javers javers() {
        return TransactionalMongoJaversBuilder.javers()
                .registerJaversRepository(new MongoRepository(mongo()))
                .withTxManager(mongoTransactionManager.orElse(null))
                .build();
    }

    /**
     * You can configure Javers' MongoRepository to use
     * your application's primary database or a dedicated database.
     */
    @Bean
    public MongoDatabase mongo() {
        return MongoClients.create().getDatabase(DATABASE_NAME);
    }

    /**
     * Required by Spring Data Mongo
     */
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(MongoClients.create(), DATABASE_NAME);
    }

    /**
     * Enables auto-audit aspect for ordinary repositories.<br/>
     *
     * Use {@link JaversAuditable}
     * to mark repository methods that you want to audit.
     */
    @Bean
    public JaversAuditableAspect javersAuditableAspect() {
        return new JaversAuditableAspect(javers(), authorProvider(), advancedCommitPropertiesProvider());
    }

    /**
     * Enables auto-audit aspect for Spring Data repositories. <br/>
     *
     * Use {@link JaversSpringDataAuditable}
     * to annotate CrudRepositories you want to audit.
     */
    @Bean
    public JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect() {
        return new JaversSpringDataAuditableRepositoryAspect(javers(), authorProvider(), advancedCommitPropertiesProvider());
    }

    /**
     * <b>INCUBATING - Javers Async API has incubating status.</b>
     * <br/><br/>
     *
     * Enables asynchronous auto-audit aspect for ordinary repositories.<br/>
     *
     * Use {@link JaversAuditableAsync}
     * to mark repository methods that you want to audit.
     */
    @Bean
    public JaversAuditableAspectAsync javersAuditableAspectAsync() {
        return new JaversAuditableAspectAsync(javers(), authorProvider(), new EmptyPropertiesProvider(), advancedCommitPropertiesProvider(), javersAsyncAuditExecutor());
    }

    /**
     * <b>INCUBATING - Javers Async API has incubating status.</b>
     * <br/><br/>
     */
    @Bean
    public ExecutorService javersAsyncAuditExecutor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("JaversAuditableAsync-%d")
                .build();
        return Executors.newFixedThreadPool(2, threadFactory);
    }

    /**
     * Required by auto-audit aspect. <br/><br/>
     *
     * Creates {@link SpringSecurityAuthorProvider} instance,
     * suitable when using Spring Security
     */
    @Bean
    public AuthorProvider authorProvider() {
        return new SpringSecurityAuthorProvider();
    }


    /**
     * Optional for auto-audit aspect. <br/>
     * @see CommitPropertiesProvider
     */
    @Bean
    public AdvancedCommitPropertiesProvider advancedCommitPropertiesProvider() {
        return new AdvancedCommitPropertiesProvider() {

            @Override
            public Map<String, String> provideForCommittedObject(AuditedMethodExecutionContext ctx, Object domainObject) {
                return Map.of(
                    "key", "ok"
                );
            }
        };
    }

}
