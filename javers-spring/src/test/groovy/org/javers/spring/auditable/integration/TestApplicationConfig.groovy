package org.javers.spring.auditable.integration


import org.javers.spring.auditable.CommitPropertiesProvider
import org.javers.spring.example.JaversSpringMongoApplicationConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@ComponentScan(basePackages = "org.javers.spring.repository")
@EnableMongoRepositories(["org.javers.spring.repository"])
@EnableAspectJAutoProxy
class TestApplicationConfig extends JaversSpringMongoApplicationConfig {

    @Bean
    CommitPropertiesProvider commitPropertiesProvider() {
        return new CommitPropertiesProvider() {
            @Override
            Map<String, String> provideForCommittedObject(Object domainObject) {
                return ["key":"ok"]
            }
        }
    }
}
