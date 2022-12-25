package org.javers.spring.auditable.integration

import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.repository.mongo.MongoRepository
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.CommitPropertiesProvider
import org.javers.spring.auditable.SpringSecurityAuthorProvider
import org.javers.spring.auditable.aspect.JaversAuditableAspect
import org.javers.spring.auditable.aspect.JaversAuditableAspectAsync
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoDatabaseUtils
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

@Configuration
@ComponentScan(basePackages = "org.javers.spring.repository")
@EnableMongoRepositories(["org.javers.spring.repository"])
@EnableAspectJAutoProxy
@EnableAutoConfiguration
class TestApplicationConfig {

    @Bean
    Javers javers(MongoDatabaseFactory dbFactory) {
        def mongoDatabase = MongoDatabaseUtils.getDatabase(dbFactory)

        MongoRepository javersMongoRepository = new MongoRepository(mongoDatabase)

        JaversBuilder.javers()
                .registerJaversRepository(javersMongoRepository)
                .build()
    }

    @Bean
    JaversAuditableAspect javersAuditableAspect() {
        new JaversAuditableAspect(javers(), authorProvider(), commitPropertiesProvider())
    }

    @Bean
    JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect() {
        new JaversSpringDataAuditableRepositoryAspect(javers(), authorProvider(),
                commitPropertiesProvider())
    }

    /**
     * <b>INCUBATING - Javers Async API has incubating status.</b>
     */
    @Bean
    JaversAuditableAspectAsync javersAuditableAspectAsync() {
        new JaversAuditableAspectAsync(javers(), authorProvider(), commitPropertiesProvider(), javersAsyncAuditExecutor())
    }

    /**
     * <b>INCUBATING - Javers Async API has incubating status.</b>
     */
    @Bean
    ExecutorService javersAsyncAuditExecutor() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("JaversAuditableAsync-%d")
                .build()
        Executors.newFixedThreadPool(2, threadFactory)
    }

    @Bean
    AuthorProvider authorProvider() {
        new SpringSecurityAuthorProvider()
    }

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
