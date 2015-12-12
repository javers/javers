package org.javers.spring.boot.mongo;

import com.github.fakemongo.Fongo;
import com.mongodb.client.MongoDatabase;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.javers.spring.boot.mongo")
@Import(JaversMongoAutoConfiguration.class)
public class TestApplication {

    @Bean
    MongoDatabase mongoDatabase() {
        return new Fongo("myDb").getDatabase("test");
    }
}
