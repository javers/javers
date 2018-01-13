package org.javers.spring.boot;

import org.javers.spring.auditable.CommitPropertiesProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pawelszymczyk
 */
@SpringBootApplication
@ComponentScan("org.javers.spring.boot.sql")
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
