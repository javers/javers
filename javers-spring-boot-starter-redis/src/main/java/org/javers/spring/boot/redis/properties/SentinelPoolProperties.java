package org.javers.spring.boot.redis.properties;

import static redis.clients.jedis.Protocol.DEFAULT_TIMEOUT;

import java.util.Set;

public class SentinelPoolProperties extends AbstractPoolProperties {

    private String masterName;
    private Set<String> sentinels;
    private int connectionTimeout = DEFAULT_TIMEOUT;
    private int soTimeout = DEFAULT_TIMEOUT;
    private int sentinelConnectionTimeout = DEFAULT_TIMEOUT;
    private int sentinelSoTimeout = DEFAULT_TIMEOUT;
    private String sentinelUser;
    private String sentinelPassword;
    private String sentinelClientName;

    public String getMasterName() {
        return masterName;
    }

    public void setMasterName(final String masterName) {
        this.masterName = masterName;
    }

    public Set<String> getSentinels() {
        return sentinels;
    }

    public void setSentinels(final Set<String> sentinels) {
        this.sentinels = sentinels;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(final int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public int getSentinelConnectionTimeout() {
        return sentinelConnectionTimeout;
    }

    public void setSentinelConnectionTimeout(final int sentinelConnectionTimeout) {
        this.sentinelConnectionTimeout = sentinelConnectionTimeout;
    }

    public int getSentinelSoTimeout() {
        return sentinelSoTimeout;
    }

    public void setSentinelSoTimeout(final int sentinelSoTimeout) {
        this.sentinelSoTimeout = sentinelSoTimeout;
    }

    public String getSentinelUser() {
        return sentinelUser;
    }

    public void setSentinelUser(final String sentinelUser) {
        this.sentinelUser = sentinelUser;
    }

    public String getSentinelPassword() {
        return sentinelPassword;
    }

    public void setSentinelPassword(final String sentinelPassword) {
        this.sentinelPassword = sentinelPassword;
    }

    public String getSentinelClientName() {
        return sentinelClientName;
    }

    public void setSentinelClientName(final String sentinelClientName) {
        this.sentinelClientName = sentinelClientName;
    }

}