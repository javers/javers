package org.javers.spring.auditable.integration

import com.mongodb.MongoClient
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.tests.MongodForTestsFactory
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
public class TestApplicationConfig extends JaversSpringMongoApplicationConfig {
    @Bean
    @Override
    MongoClient mongo() {
        return MongodForTestsFactory.with(Version.Main.PRODUCTION).newMongo()
    }
}
