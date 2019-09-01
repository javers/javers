package org.javers.spring.boot;

import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.CommitPropertiesProviderContext;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Collections;
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
        final Map<String, String> rv = new HashMap<>();
        rv.put("key", "ok");
        return new CommitPropertiesProvider() {
            @Override
            public Map<String, String> provide(CommitPropertiesProviderContext context, Object domainObject) {
                if (domainObject instanceof EmployeeEntity) {
                    if (context == CommitPropertiesProviderContext.SAVE_UPDATE) {
                        Map<String, String> saveProperties = new HashMap<>();
                        saveProperties.put("departmentId", ((EmployeeEntity) domainObject).getDepartment().getId().toString());
                        saveProperties.put("employeeId", ((EmployeeEntity) domainObject).getId().toString());
                        return saveProperties;
                    } else if (context == CommitPropertiesProviderContext.DELETE) {
                        Map<String, String> deleteProperties = new HashMap<>();
                        deleteProperties.put("departmentId", ((EmployeeEntity) domainObject).getDepartment().getId().toString());
                        return deleteProperties;
                    }
                } else if (domainObject instanceof DummyEntity) {
                    ShallowEntity shallowEntity = ((DummyEntity) domainObject).getShallowEntity();
                    if (shallowEntity != null) {
                        Map<String, String> saveProperties = new HashMap<>();
                        saveProperties.put("shallowId", String.valueOf(shallowEntity.getId()));
                        return saveProperties;
                    }
                }
                return Collections.unmodifiableMap(rv);
            }

            @Override
            public Map<String, String> provideForDeleteById(Class<?> domainObjectClass, Object domainObjectId) {
                if (domainObjectClass.isAssignableFrom(EmployeeEntity.class)) {
                    Map<String, String> deleteProperties = new HashMap<>();
                    deleteProperties.put("employeeId", domainObjectId.toString());
                    return deleteProperties;
                }
                return Collections.unmodifiableMap(rv);
            }
        };
    }
}
