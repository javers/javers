package org.javers.spring.boot.redis.properties;

import java.time.Duration;

public class RedisProperties {

    private boolean enabled = true;
    private JedisPoolProperties jedis = new JedisPoolProperties();
    private SentinelPoolProperties sentinel;
    private Duration auditDuration = Duration.ofDays(30);

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public JedisPoolProperties getJedis() {
        return jedis;
    }

    public void setJedis(final JedisPoolProperties jedis) {
        this.jedis = jedis;
    }

    public SentinelPoolProperties getSentinel() {
        return sentinel;
    }

    public void setSentinel(final SentinelPoolProperties sentinel) {
        this.sentinel = sentinel;
    }

    public Duration getAuditDuration() {
        return auditDuration;
    }

    public void setAuditDuration(final Duration auditDuration) {
        this.auditDuration = auditDuration;
    }

}