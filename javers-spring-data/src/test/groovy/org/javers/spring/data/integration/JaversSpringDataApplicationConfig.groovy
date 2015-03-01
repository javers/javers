package org.javers.spring.data.integration

import com.github.fakemongo.Fongo
import com.mongodb.Mongo
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.data.aspect.JaversSpringDataRepositoryAspect
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
@EnableMongoRepositories(basePackages = "org.javers.spring.data.integration")
public class JaversSpringDataApplicationConfig {

    @Bean
    public Javers javers() {
        return JaversBuilder.javers().build()
    }

    @Bean
    public AuthorProvider authorProvider() {
        return { "author" } as AuthorProvider
    }

    @Bean
    public JaversSpringDataRepositoryAspect springDataRepositoryAspect() {
        return new JaversSpringDataRepositoryAspect(javers(), authorProvider())
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
