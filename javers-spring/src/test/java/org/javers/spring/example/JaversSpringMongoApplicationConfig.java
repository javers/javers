package org.javers.spring.example;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoDatabase;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.repository.mongo.MongoRepository;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;
import org.javers.spring.auditable.aspect.JaversAuditableRepositoryAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author bartosz walacik
 */
@Configuration
@ComponentScan(basePackages = "org.javers.spring.repository.mongo")
@EnableAspectJAutoProxy
@EnableMongoRepositories(basePackages = "org.javers.spring.repository.mongo")
public class JaversSpringMongoApplicationConfig {

    /**
     * Creates JaVers instance backed by {@link MongoRepository}
     */
    @Bean
    public Javers javers() {
        MongoRepository javersMongoRepository = new MongoRepository(mongoDB());

        return JaversBuilder.javers()
                .registerJaversRepository(javersMongoRepository)
                .build();
    }

    /**
     * MongoDB setup
     */
    @Bean
    public MongoDatabase mongoDB() {
        return new Fongo("test").getDatabase("test");
    }

    /**
     * Enables Repository auto-audit aspect. <br/>
     *
     * Use {@link org.javers.spring.annotation.JaversSpringDataAuditable}
     * to annotate Spring Data Repositories
     * or {@link org.javers.spring.annotation.JaversAuditable} for ordinary Repositories.
     */
    @Bean
    public JaversAuditableRepositoryAspect javersAuditableRepositoryAspect() {
        return new JaversAuditableRepositoryAspect(javers(), authorProvider());
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
}
