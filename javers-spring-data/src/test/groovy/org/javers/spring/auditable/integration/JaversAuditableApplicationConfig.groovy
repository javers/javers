package org.javers.spring.auditable.integration

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.aspect.JaversAuditableMethodAspect
import org.javers.spring.auditable.integration.DummyAuditedRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

/**
 * @author Pawel Szymczyk
 */
@Configuration
@ComponentScan
@EnableAspectJAutoProxy(proxyTargetClass = true)
class JaversAuditableApplicationConfig {

    @Bean
    public DummyAuditedRepository dummyObjectRepository() {
        return new DummyAuditedRepository()
    }

    @Bean
    public Javers javers() {
        return JaversBuilder.javers().build()
    }

    @Bean
    public AuthorProvider authorProvider() {
        return { "author" } as AuthorProvider
    }

    @Bean
    public JaversAuditableMethodAspect auditableMethodAspect() {
        return new JaversAuditableMethodAspect(javers(), authorProvider())
    }
}
