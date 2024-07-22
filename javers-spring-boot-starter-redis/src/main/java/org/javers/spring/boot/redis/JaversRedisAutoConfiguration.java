package org.javers.spring.boot.redis;

import java.util.List;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.JaversBuilderPlugin;
import org.javers.repository.redis.JaversRedisRepository;
import org.javers.spring.RegisterJsonTypeAdaptersPlugin;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.EmptyPropertiesProvider;
import org.javers.spring.auditable.MockAuthorProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConditionalOnProperty("javers.redis.enabled")
@EnableAspectJAutoProxy
@EnableConfigurationProperties({JaversRedisProperties.class})
@Import({RegisterJsonTypeAdaptersPlugin.class})
public class JaversRedisAutoConfiguration {

    @Bean(destroyMethod = "destroy")
    JedisPool jedisPool(final JaversRedisProperties javersRedisProperties) {
        final var redisProps = javersRedisProperties.getRedis();
        final var jedisPoolConfig = new JedisPoolConfig();
        return new JedisPool(jedisPoolConfig,
                redisProps.getHost(), redisProps.getPort(), redisProps.getTimeout(),
                redisProps.getPassword(), redisProps.getDatabase(), redisProps.isUseSsl());
    }

    @Bean(destroyMethod = "shutdown")
    JaversRedisRepository javersRedisRepository(final JaversRedisProperties javersRedisProperties, JedisPool jedisPool) {
        return new JaversRedisRepository(jedisPool, javersRedisProperties.getRedis().getAuditDuration());
    }

    @Bean
    @ConditionalOnMissingBean
    Javers javers(final JaversRedisProperties javersRedisProperties, final JaversRedisRepository javersRedisRepository,
            final List<JaversBuilderPlugin> plugins) {
        final var javersBuilder = JaversBuilder.javers()
                .registerJaversRepository(javersRedisRepository)
                .withProperties(javersRedisProperties)
                .withObjectAccessHook(javersRedisProperties.createObjectAccessHookInstance());
        plugins.forEach(plugin -> plugin.beforeAssemble(javersBuilder));
        return javersBuilder.build();
    }

    @Bean(name = "springSecurityAuthorProvider")
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = {"org.springframework.security.core.context.SecurityContextHolder"})
    AuthorProvider springSecurityAuthorProvider() {
        return new SpringSecurityAuthorProvider();
    }

    @Bean(name = "mockAuthorProvider")
    @ConditionalOnMissingBean
    @ConditionalOnMissingClass({"org.springframework.security.core.context.SecurityContextHolder"})
    AuthorProvider unknownAuthorProvider() {
        return new MockAuthorProvider();
    }

    @Bean(name = "emptyPropertiesProvider")
    @ConditionalOnMissingBean
    CommitPropertiesProvider emptyPropertiesProvider() {
        return new EmptyPropertiesProvider();
    }

    @Bean
    @ConditionalOnProperty(name = "javers.auditableAspectEnabled", havingValue = "true", matchIfMissing = true)
    JaversAuditableAspect javersAuditableAspect(final Javers javers, final AuthorProvider authorProvider,
            final CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversAuditableAspect(javers, authorProvider, commitPropertiesProvider);
    }

    @Bean
    @ConditionalOnProperty(name = "javers.springDataAuditableRepositoryAspectEnabled", havingValue = "true", matchIfMissing = true)
    JaversSpringDataAuditableRepositoryAspect javersSpringDataAuditableAspect(final Javers javers, final AuthorProvider authorProvider,
            final CommitPropertiesProvider commitPropertiesProvider) {
        return new JaversSpringDataAuditableRepositoryAspect(javers, authorProvider, commitPropertiesProvider);
    }
}
