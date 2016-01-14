package org.javers.spring.boot.mongo;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.javers.spring.boot.mongo")
public class TestApplication {

    @Bean
    MongoClient mongoClient() {
        return new Fongo("myDb").getMongo();
    }
}
