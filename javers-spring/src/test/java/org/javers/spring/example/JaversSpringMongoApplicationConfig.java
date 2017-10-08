package org.javers.spring.example;

import com.github.fakemongo.Fongo;
import com.google.common.collect.ImmutableMap;
import com.mongodb.MongoClient;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.repository.mongo.MongoRepository;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

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
    @Bean
    public MongoClient mongo() {
        return new Fongo("test").getMongo();
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
     * Use {@link org.javers.spring.annotation.JaversAuditable}
     * to mark data writing methods that you want to audit.
     */
    @Bean
    public JaversAuditableAspect javersAuditableAspect() {
        return new JaversAuditableAspect(javers(), authorProvider(), commitPropertiesProvider());
    }

    /**
     * Enables auto-audit aspect for Spring Data repositories. <br/>
     *
     * Use {@link org.javers.spring.annotation.JaversSpringDataAuditable}
     * to annotate CrudRepositories you want to audit.
     */
    @Bean
    public JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect() {
        return new JaversSpringDataAuditableRepositoryAspect(javers(), authorProvider(),
                commitPropertiesProvider());
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
        return () -> ImmutableMap.of("key", "ok");
    }
}
