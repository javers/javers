package org.javers.spring.boot;

import org.apache.groovy.util.Maps;
import org.javers.core.json.BasicStringTypeAdapter;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author pawelszymczyk
 */
@SpringBootApplication
@ComponentScan({"org.javers.spring.boot.sql","org.javers.spring.transactions"})
public class TestApplication {
    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {
            @Override
            public Map<String, String> provide() {
                return Maps.of("deprecated commitPropertiesProvider.provide()", "still works");
            }
        };
    }

    public static class DummyBigDecimalEntity {
        BigDecimal value;

        DummyBigDecimalEntity(BigDecimal value) {
            this.value = value;
        }
    }

    @Bean
    JsonTypeAdapter dummyEntityJsonTypeAdapter () {

        return new BasicStringTypeAdapter<DummyBigDecimalEntity>() {
            @Override
            public String serialize(DummyBigDecimalEntity sourceValue) {
                return sourceValue.value.toString();
            }

            @Override
            public DummyBigDecimalEntity deserialize(String serializedValue) {
                return new DummyBigDecimalEntity(new BigDecimal(serializedValue));
            }

            @Override
            public Class getValueType() {
                return DummyBigDecimalEntity.class;
            }
        };
    }
}
