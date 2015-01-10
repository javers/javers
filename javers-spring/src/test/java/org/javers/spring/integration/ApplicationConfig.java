package org.javers.spring.integration;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.spring.AuthorProvider;
import org.javers.spring.JaversPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Pawel Szymczyk
 */
@Configuration
@ComponentScan
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ApplicationConfig {

    @Bean
    public ProjectRepository projectRepository() throws Exception {
        return new ProjectRepository();
    }

    @Bean
    public UserRepository userRepository() throws Exception {
        return new UserRepository();
    }

    @Bean
    public Javers javers() {
        return JaversBuilder.javers().build();
    }

    @Bean
    public AuthorProvider authorProvider() {
        return new DummyAuthorProvider();
    }

    @Bean
    public JaversPostProcessor javersPostProcessor() {
        return new JaversPostProcessor(javers(), authorProvider());
    }
}
