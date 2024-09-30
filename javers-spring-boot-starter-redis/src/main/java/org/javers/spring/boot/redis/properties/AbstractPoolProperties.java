package org.javers.spring.boot.redis.properties;

import redis.clients.jedis.Protocol;

public abstract class AbstractPoolProperties {

    private String user;
    private String password;
    private int database = Protocol.DEFAULT_DATABASE;
    private String clientName;

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(final int database) {
        this.database = database;
    }


    public String getClientName() {
      return clientName;
    }

    public void setClientName(final String clientName) {
      this.clientName = clientName;
    }
}
