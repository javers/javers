package org.javers.spring.boot.redis;

import java.util.List;
import java.util.Objects;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.JaversBuilderPlugin;
import org.javers.repository.redis.JaversRedisRepository;
import org.javers.spring.JaversSpringProperties;
import org.javers.spring.RegisterJsonTypeAdaptersPlugin;
import org.javers.spring.auditable.AuthorProvider;
import org.javers.spring.auditable.CommitPropertiesProvider;
import org.javers.spring.auditable.EmptyPropertiesProvider;
import org.javers.spring.auditable.MockAuthorProvider;
import org.javers.spring.auditable.SpringSecurityAuthorProvider;
import org.javers.spring.auditable.aspect.JaversAuditableAspect;
import org.javers.spring.auditable.aspect.springdata.JaversSpringDataAuditableRepositoryAspect;
import org.javers.spring.boot.redis.properties.JaversRedisProperties;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.util.Pool;

@Configuration
@ConditionalOnProperty(prefix = "javers.redis", name = "enabled", matchIfMissing = true)
@EnableAspectJAutoProxy
@EnableConfigurationProperties({JaversRedisProperties.class})
@Import({RegisterJsonTypeAdaptersPlugin.class})
public class JaversRedisAutoConfiguration {

    @Bean(destroyMethod = "destroy")
    @ConditionalOnMissingBean
    Pool<Jedis> javersJedisPool(final JaversRedisProperties javersRedisProperties) {
        if (Objects.nonNull(javersRedisProperties.getRedis().getSentinel())) {
            final var jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setJmxEnabled(false);
            final var sentinelPoolProps = javersRedisProperties.getRedis().getSentinel();
            return new JedisSentinelPool(
                    sentinelPoolProps.getMasterName(),
                    sentinelPoolProps.getSentinels(),
                    jedisPoolConfig,
                    sentinelPoolProps.getConnectionTimeout(),
                    sentinelPoolProps.getSoTimeout(),
                    sentinelPoolProps.getUser(),
                    sentinelPoolProps.getPassword(),
                    sentinelPoolProps.getDatabase(),
                    sentinelPoolProps.getClientName(),
                    sentinelPoolProps.getSentinelConnectionTimeout(),
                    sentinelPoolProps.getSentinelSoTimeout(),
                    sentinelPoolProps.getSentinelUser(),
                    sentinelPoolProps.getSentinelPassword(),
                    sentinelPoolProps.getSentinelClientName());
        } else {
            final var jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setJmxEnabled(false);
            final var jedisPoolProps = javersRedisProperties.getRedis().getJedis();
            return new JedisPool(jedisPoolConfig,
                    jedisPoolProps.getHost(),
                    jedisPoolProps.getPort(),
                    jedisPoolProps.getTimeout(),
                    jedisPoolProps.getUser(),
                    jedisPoolProps.getPassword(),
                    jedisPoolProps.getDatabase(),
                    jedisPoolProps.getClientName(),
                    jedisPoolProps.isSsl());
        }
    }

    @Bean(destroyMethod = "shutdown")
    JaversRedisRepository javersRedisRepository(final Pool<Jedis> javersJedisPool, final JaversRedisProperties javersRedisProperties) {
        if (javersJedisPool instanceof final JedisPool jedisPool) {
            return new JaversRedisRepository(jedisPool, javersRedisProperties.getRedis().getAuditDuration());
        }
        if (javersJedisPool instanceof final JedisSentinelPool sentinelPool) {
            return new JaversRedisRepository(sentinelPool, javersRedisProperties.getRedis().getAuditDuration());
        }
        throw new BeanCreationException("Unsupported Javers Jedis Pool:" + javersJedisPool.getClass().getName());
    }

    @Bean
    @ConditionalOnMissingBean
    Javers javers(final JaversSpringProperties javersSpringProperties, final JaversRedisRepository javersRedisRepository,
            final List<JaversBuilderPlugin> plugins) {
        final var objectAccessHook = javersSpringProperties.createObjectAccessHookInstance();
        final var javersBuilder = JaversBuilder.javers()
                .registerJaversRepository(javersRedisRepository)
                .withProperties(javersSpringProperties)
                .withObjectAccessHook(objectAccessHook);

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
