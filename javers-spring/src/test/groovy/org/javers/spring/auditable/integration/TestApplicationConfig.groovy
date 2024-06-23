package org.javers.spring.auditable.integration

import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.repository.mongo.MongoRepository
import org.javers.spring.auditable.AdvancedCommitPropertiesProvider
import org.javers.spring.auditable.AuditedMethodExecutionContext
import org.javers.spring.auditable.AuthorProvider
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
        new JaversAuditableAspect(javers(), authorProvider(), advancedCommitPropertiesProvider())
    }

    @Bean
    JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect() {
        new JaversSpringDataAuditableRepositoryAspect(javers(), authorProvider(), advancedCommitPropertiesProvider())
    }

    /**
     * <b>INCUBATING - Javers Async API has incubating status.</b>
     */
    @Bean
    JaversAuditableAspectAsync javersAuditableAspectAsync() {
        new JaversAuditableAspectAsync(javers(), authorProvider(), advancedCommitPropertiesProvider(), javersAsyncAuditExecutor())
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
    AdvancedCommitPropertiesProvider advancedCommitPropertiesProvider() {
        return new AdvancedCommitPropertiesProvider() {

            @Override
            Map<String, String> provideForCommittedObject(Object domainObject) {
                ["key":"ok"]
            }
            @Override
            Map<String, String> provideForDeletedObject(Object domainObject) {
                ["key":"ok_deleted"]
            }

            @Override
            Map<String, String> provideForCommittedObject(Object domainObject, AuditedMethodExecutionContext ctx) {
                [
                    "TargetMethodName" : ctx.getTargetMethodName(),
                    "TargetClassName" : ctx.getTargetClassName(),
                    "TargetMethodArgs.size" : ctx.getTargetMethodArgs().length + ''
                ]
            }

            @Override
            Map<String, String> provideForDeletedObject(Object domainObject, AuditedMethodExecutionContext ctx) {
                [
                    "TargetMethodName" : ctx.getTargetMethodName(),
                    "TargetClassName" : ctx.getTargetClassName(),
                ]
            }

            @Override
            public Map<String, String> provideForDeleteById(Class<?> domainObjectClass, Object domainObjectId, AuditedMethodExecutionContext ctx) {
                [
                    "TargetMethodName" : ctx.getTargetMethodName(),
                    "TargetClassName" : ctx.getTargetClassName(),
                ]
            }

        };
    }

}
