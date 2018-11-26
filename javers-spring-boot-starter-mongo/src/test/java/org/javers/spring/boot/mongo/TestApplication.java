package org.javers.spring.boot.mongo;

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
@ComponentScan("org.javers.spring.boot.mongo")
public class TestApplication {

    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {
            @Override
            public Map<String, String> provide() {
                Map<String, String> props = new HashMap<>();
                props.put("key", "ok");
                return props;
            }
        };
    }
}
