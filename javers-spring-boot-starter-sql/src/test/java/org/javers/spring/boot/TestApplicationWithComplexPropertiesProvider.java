package org.javers.spring.boot;

import org.apache.groovy.util.Maps;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@ComponentScan("org.javers.spring.boot.sql")
public class TestApplicationWithComplexPropertiesProvider {
    @Bean
    public CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {
            @Override
            public Map<String, String> provideForCommittedObject(Object domainObject) {
                if (domainObject instanceof EmployeeEntity) {
                    EmployeeEntity emp = (EmployeeEntity) domainObject;
                    Map<String, String> saveProperties = new HashMap<>();
                    saveProperties.put("employeeId", emp.getId().toString());

                    if (emp.getDepartment() != null) {
                        saveProperties.put("departmentId", emp.getDepartment().getId().toString());

                    }
                    return saveProperties;
                } else if (domainObject instanceof ShallowEntity) {
                    return Maps.of("ShallowEntity.value", ((ShallowEntity) domainObject).getValue());
                }
                return Collections.emptyMap();
            }

            @Override
            public Map<String, String> provideForDeletedObject(Object domainObject) {
                if (domainObject instanceof EmployeeEntity) {
                    return Maps.of("deleted employeeId", ((EmployeeEntity) domainObject).getId().toString());
                }
                return Collections.emptyMap();
            }

            @Override
            public Map<String, String> provideForDeleteById(Class<?> domainObjectClass, Object domainObjectId) {
                if (domainObjectClass.isAssignableFrom(EmployeeEntity.class)) {
                    return Maps.of("employee deletedById", domainObjectId.toString());
                }
                return Collections.emptyMap();
            }

            @Override
            public Map<String, String> provide() {
                return Maps.of("commit", "seems fine");
            }
        };
    }
}
