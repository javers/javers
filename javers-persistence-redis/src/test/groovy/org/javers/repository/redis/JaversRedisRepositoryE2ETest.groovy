package org.javers.repository.redis

import org.javers.core.JaversRepositoryShadowE2ETest
import org.javers.core.JaversTestBuilder
import org.javers.repository.api.JaversRepository
import org.testcontainers.spock.Testcontainers
import redis.clients.jedis.Jedis
import redis.clients.jedis.args.FlushMode
import spock.lang.Shared
import spock.lang.Stepwise

import java.time.Duration

@Testcontainers
@Stepwise
class JaversRedisRepositoryE2ETest extends JaversRepositoryShadowE2ETest {

    @Shared
    dockerizedRedisContainer = new DockerizedRedisContainer()

    @Shared
    javersTestBuilder = JaversTestBuilder.javersTestAssembly()

    @Shared
    javersRepository = new JaversRedisRepository(dockerizedRedisContainer.getJedisPool(), Duration.ofMinutes(20))

    def cleanup() {
        Jedis jedis
        try {
            jedis = dockerizedRedisContainer.getJedisPool().getResource()
            jedis.flushAll(FlushMode.SYNC)
            jedis.close()
        } catch (Exception e) {
            println("Error occurred while flushing Redis after test: ${e.message}")
            throw e
        } finally {
            jedis.close()
        }
    }

    @Override
    protected JaversRepository prepareJaversRepository() {
        return javersRepository
    }
}
