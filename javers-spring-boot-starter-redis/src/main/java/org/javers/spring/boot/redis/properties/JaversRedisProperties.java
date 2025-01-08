package org.javers.spring.boot.redis.properties;

import org.javers.spring.JaversSpringProperties;
import org.javers.spring.boot.redis.RedisObjectAccessHook;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "javers")
public class JaversRedisProperties extends JaversSpringProperties {

    private RedisProperties redis;

    @Override
    protected String defaultObjectAccessHook() {
        if (getObjectAccessHook() != null) {
            return getObjectAccessHook();
        }
        return RedisObjectAccessHook.class.getName();
    }

    public RedisProperties getRedis() {
        return redis;
    }

    public void setRedis(final RedisProperties redis) {
        this.redis = redis;
    }

}
