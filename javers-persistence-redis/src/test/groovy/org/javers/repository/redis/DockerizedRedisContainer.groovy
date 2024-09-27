package org.javers.repository.redis

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.MountableFile
import redis.clients.jedis.JedisPool

class DockerizedRedisContainer {
    GenericContainer redisContainer
    JedisPool jedisPool

    DockerizedRedisContainer() {
        this.redisContainer = startRedis()
        def jedisPoolConfig = new GenericObjectPoolConfig()
        jedisPoolConfig.setMaxTotal(4)
        jedisPoolConfig.setBlockWhenExhausted(false)
        this.jedisPool = new JedisPool(jedisPoolConfig, getRedisHost(), getRedisPort())
    }

    GenericContainer startRedis() {
        def redisContainer = new GenericContainer<>("redis:7-alpine")
                .withExposedPorts(6379)
                .withCommand("redis-server", "/etc/redis/redis.conf")
                .withCopyFileToContainer(MountableFile.forClasspathResource("redis.conf"), "/etc/redis/redis.conf")
        redisContainer.start()
        return redisContainer
    }

    String getRedisHost() {
        return redisContainer.host
    }

    Integer getRedisPort() {
        return redisContainer.getMappedPort(6379)
    }

    JedisPool getJedisPool() {
        return jedisPool
    }

}
