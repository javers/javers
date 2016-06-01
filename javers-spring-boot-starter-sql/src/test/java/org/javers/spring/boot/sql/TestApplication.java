package org.javers.spring.boot.sql;

import com.google.common.collect.Maps;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

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
                Map props = new HashMap();
                props.put("key", "ok");
                return props;
            }
        };
    }
}
