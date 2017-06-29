package org.javers.api;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.javers.api")
public class TestApplication {

    @Bean
    public MongoClient mongoClient() {
        return new Fongo("myDb").getMongo();
    }

    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return () -> {
            Map<String, String> props = new HashMap<>();
            props.put("key", "ok");
            return props;
        };
    }
}
