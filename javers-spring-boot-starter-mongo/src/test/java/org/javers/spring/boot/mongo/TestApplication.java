package org.javers.spring.boot.mongo;

import org.javers.common.collections.Maps;
import org.javers.core.JaversBuilderPlugin;
import org.javers.core.diff.custom.CustomBigDecimalComparator;
import org.javers.core.json.BasicStringTypeAdapter;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

/**
 * @author pawelszymczyk
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan({"org.javers.spring.boot.mongo", "org.javers.spring.transactions"})
public class TestApplication {

    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {
            @Override
            public Map<String, String> provideForCommittedObject(Object domainObject) {
                if (domainObject instanceof DummyEntity) {
                    return Maps.of("dummyEntityId", ((DummyEntity)domainObject).getId() + "");
                }
                return Collections.emptyMap();
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

    @Bean
    JaversBuilderPlugin javersBuilderPlugin() {
        return builder -> builder
                .registerValue(BigDecimal.class, new CustomBigDecimalComparator(2));
    }
}
