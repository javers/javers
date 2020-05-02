package org.javers.spring.example;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.javers.common.collections.Maps;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.repository.mongo.MongoRepository;
import org.javers.spring.annotation.JaversAuditable;
import org.javers.spring.annotation.JaversAuditableAsync;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.JaversAuditableAspectAsync;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Configuration
@ComponentScan(basePackages = "org.javers.spring.repository")
@EnableMongoRepositories({"org.javers.spring.repository"})
@EnableAspectJAutoProxy
public class JaversSpringMongoApplicationConfig {
    private static final String DATABASE_NAME = "mydatabase";

    /**
     * Creates JaVers instance backed by {@link MongoRepository}
     */
    @Bean
    public Javers javers() {
        MongoRepository javersMongoRepository =
                new MongoRepository(mongo().getDatabase(DATABASE_NAME));

        return JaversBuilder.javers()
                .registerJaversRepository(javersMongoRepository)
                .build();
    }

    /**
     * MongoDB setup
     */
    @Bean(name="realMongoClient")
    @ConditionalOnMissingBean
    public MongoClient mongo() {
        return MongoClients.create();
    }

    /**
     * required by Spring Data MongoDB
     */
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), DATABASE_NAME);
    }

    /**
     * Enables auto-audit aspect for ordinary repositories.<br/>
     *
     * Use {@link JaversAuditable}
     * to mark repository methods that you want to audit.
     */
    @Bean
    public JaversAuditableAspect javersAuditableAspect() {
        return new JaversAuditableAspect(javers(), authorProvider(), commitPropertiesProvider());
    }

    /**
     * Enables auto-audit aspect for Spring Data repositories. <br/>
     *
     * Use {@link JaversSpringDataAuditable}
     * to annotate CrudRepositories you want to audit.
     */
    @Bean
    public JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect() {
        return new JaversSpringDataAuditableRepositoryAspect(javers(), authorProvider(),
                commitPropertiesProvider());
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
        return new JaversAuditableAspectAsync(javers(), authorProvider(), commitPropertiesProvider(), javersAsyncAuditExecutor());
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
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {
            @Override
            public Map<String, String> provideForCommittedObject(Object domainObject) {
                if (domainObject instanceof DummyObject) {
                    return Maps.of("dummyObject.name", ((DummyObject)domainObject).getName());
                }
                return Collections.emptyMap();
            }
        };
    }
}
