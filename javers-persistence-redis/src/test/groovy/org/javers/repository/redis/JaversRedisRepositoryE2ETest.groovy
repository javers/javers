package org.javers.repository.redis

import org.javers.core.JaversRepositoryShadowE2ETest
import org.javers.core.JaversTestBuilder
import org.javers.repository.api.JaversRepository
import org.testcontainers.spock.Testcontainers
import redis.clients.jedis.Jedis
import spock.lang.Shared

import java.time.Duration

@Testcontainers
class JaversRedisRepositoryE2ETest extends JaversRepositoryShadowE2ETest {

    @Shared
    dockerizedRedisContainer = new DockerizedRedisContainer()

    @Shared
    redisRepository = new JaversRedisRepository(dockerizedRedisContainer.getJedisPool(), Duration.ofMinutes(1))

    @Shared
    javersTestBuilder = JaversTestBuilder.javersTestAssembly()

    @Override
    def setup() {
        repository.jsonConverter = javers.jsonConverter
        javersTestBuilder = JaversTestBuilder.javersTestAssembly()
    }

    def cleanup() {
        try (Jedis jedis = dockerizedRedisContainer.getJedisPool().getResource()) {
            jedis.flushAll()
        } catch (Exception e) {
            println("Error occurred while flushing Redis after test: ${e.message}")
            throw e
        }
    }

    @Override
    protected JaversRepository prepareJaversRepository() {
        return redisRepository
    }

}
