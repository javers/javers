package org.javers.spring.auditable.aspect;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author bartosz walacik
 */
@Configuration
@ComponentScan
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ExampleApplicationConfig {

    @Bean
    public AuthorProvider authorProvider() {
        return new SpringSecurityAuthorProvider();
    }

    @Bean
    public JaversAuditableMethodAspect auditableMethodAspect() {
        return new JaversAuditableMethodAspect(javers(), authorProvider());
    }

    @Bean
    public Javers javers() {
        return JaversBuilder.javers().build();
    }
}
