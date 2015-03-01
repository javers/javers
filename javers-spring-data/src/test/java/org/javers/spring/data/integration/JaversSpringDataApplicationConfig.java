package org.javers.spring.data.integration;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.spring.AuthorProvider;
import org.javers.spring.data.aspect.JaversSpringDataRepositoryAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by gessnerfl on 22.02.15.
 */
@Configuration
@ComponentScan
@EnableAspectJAutoProxy
@EnableMongoRepositories(basePackages = "org.javers.spring.data.integration")
public class JaversSpringDataApplicationConfig {
    private static final String DB_NAME = "dummy";

    @Bean
    public Javers javers() {
        return JaversBuilder.javers().build();
    }

    @Bean
    public AuthorProvider authorProvider() {
        return new AuthorProvider() {
            public String provide() {
                return "author";
            }
        };
    }

    @Bean
    public JaversSpringDataRepositoryAspect postProcessor() {
        return new JaversSpringDataRepositoryAspect(javers(), authorProvider());
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongo(), DB_NAME);
    }

    @Bean
    public Mongo mongo() {
        return new Fongo("InMemoryMongo").getMongo();
    }
}
