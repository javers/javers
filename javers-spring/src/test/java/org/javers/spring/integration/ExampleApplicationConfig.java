package org.javers.spring.integration;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.spring.AuthorProvider;
import org.javers.spring.JaversPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
    public JaversPostProcessor javersPostProcessor() {
        return new JaversPostProcessor(javers(), authorProvider());
    }

    @Bean
    public Javers javers() {
        return JaversBuilder.javers().build();
    }

    private static class SpringSecurityAuthorProvider implements AuthorProvider {
        @Override
        public String provide() {
            Authentication auth =  SecurityContextHolder.getContext().getAuthentication();

            if (auth == null) {
                return "unauthenticated";
            }

            return auth.getName();
        }
    }
}
