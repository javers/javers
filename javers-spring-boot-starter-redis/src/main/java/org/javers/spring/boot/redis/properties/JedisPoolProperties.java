package org.javers.spring.boot.redis.properties;

import static redis.clients.jedis.Protocol.DEFAULT_HOST;
import static redis.clients.jedis.Protocol.DEFAULT_PORT;
import static redis.clients.jedis.Protocol.DEFAULT_TIMEOUT;

public class JedisPoolProperties extends AbstractPoolProperties {

    private String host = DEFAULT_HOST;
    private int port = DEFAULT_PORT;
    private int timeout = DEFAULT_TIMEOUT;
    private boolean ssl;

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(final boolean ssl) {
        this.ssl = ssl;
    }

}