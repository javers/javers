package org.javers.api;

import org.javers.core.Javers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author pawel szymczyk
 */
@Configuration
@ConditionalOnProperty(name = "javers.api.enabled", havingValue = "true", matchIfMissing = true)
public class JaversApiAutoConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private Javers javers;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new SnapshotResponseMessageConverter(javers));
        super.configureMessageConverters(converters);
    }

    @Bean
    public JaversApiController javersApiController() {
        return new JaversApiController(new JaversQueryService(javers));
    }
}
