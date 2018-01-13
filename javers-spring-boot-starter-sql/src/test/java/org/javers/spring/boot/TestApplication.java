package org.javers.spring.boot;

import java.util.HashMap;
import java.util.Map;

import org.javers.spring.auditable.CommitPropertiesProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author pawelszymczyk
 */
@EnableAutoConfiguration
@EnableTransactionManagement
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
