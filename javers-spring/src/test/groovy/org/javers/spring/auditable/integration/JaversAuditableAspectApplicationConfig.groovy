package org.javers.spring.auditable.integration

import com.github.fakemongo.Fongo
import com.mongodb.Mongo
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.aspect.JaversAuditableRepositoryAspect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

/**
 * Created by gessnerfl on 22.02.15.
 */
@Configuration
@ComponentScan
@EnableAspectJAutoProxy
@EnableMongoRepositories(basePackages = "org.javers.spring.auditable.integration")
public class JaversAuditableAspectApplicationConfig {

    @Bean
    public Javers javers() {
        return JaversBuilder.javers().build()
    }

    @Bean
    public AuthorProvider authorProvider() {
        return { "author" } as AuthorProvider
    }

    @Bean
    public JaversAuditableRepositoryAspect javersAuditableRepositoryAspect() {
        return new JaversAuditableRepositoryAspect(javers(), authorProvider())
    }

    @Bean
    public DummyAuditedRepository dummyAuditedRepository() {
        return new DummyAuditedRepository()
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongo(), "dummy")
    }

    @Bean
    public Mongo mongo() {
        return new Fongo("InMemoryMongo").mongo
    }
}
