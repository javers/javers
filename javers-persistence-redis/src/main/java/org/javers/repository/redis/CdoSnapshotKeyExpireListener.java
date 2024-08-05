package org.javers.repository.redis;

import static org.javers.repository.redis.JaversRedisRepository.JV_SNAPSHOTS;
import static org.javers.repository.redis.JaversRedisRepository.JV_SNAPSHOTS_ENTITY_KEYS;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

public class CdoSnapshotKeyExpireListener extends JedisPubSub {

    private final JedisPool jedisPool;

    public CdoSnapshotKeyExpireListener(final JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        if ("expired".equals(message)) {
            final var key = channel.substring(channel.indexOf(JV_SNAPSHOTS));
            final var entityName = key.substring(13, key.indexOf("/"));
            try (final var jedis = jedisPool.getResource()) {
                jedis.srem(JV_SNAPSHOTS_ENTITY_KEYS, key);
                jedis.srem(JV_SNAPSHOTS_ENTITY_KEYS.concat(":").concat(entityName), key);
            }
        }
    }

}
