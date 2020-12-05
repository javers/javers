package org.javers.spring.boot.mongo;

import org.javers.spring.JaversSpringProperties;
import org.javers.spring.mongodb.DBRefUnproxyObjectAccessHook;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author pawelszymczyk
 */
@ConfigurationProperties(prefix = "javers")
public class JaversMongoProperties extends JaversSpringProperties {
    private static final String DEFAULT_OBJECT_ACCESS_HOOK = DBRefUnproxyObjectAccessHook.class.getName();

    private boolean documentDbCompatibilityEnabled = false;

    // Set 0 to disable.
    private int snapshotsCacheSize = 5000;

    private Mongodb mongodb;

    public boolean isDocumentDbCompatibilityEnabled() {
        return documentDbCompatibilityEnabled;
    }

    public void setDocumentDbCompatibilityEnabled(boolean documentDbCompatibilityEnabled) {
        this.documentDbCompatibilityEnabled = documentDbCompatibilityEnabled;
    }

    public int getSnapshotsCacheSize() {
        return snapshotsCacheSize;
    }

    public void setSnapshotsCacheSize(final int cacheSize) {
        this.snapshotsCacheSize = cacheSize;
    }

    public Mongodb getMongodb() {
        return mongodb;
    }

    public void setMongodb(Mongodb mongodb) {
        this.mongodb = mongodb;
    }

    /**
     * If <code>javers.mongodb</code> configuration is non-empty,
     * Javers uses it to connect to the dedicated MongoDB.
     * <br/>
     * Otherwise, Javers reuses application's MongoDB configured in the standard way
     * by <code>spring-boot-starter-data-mongodb</code>
     * (typically defined in <code>spring.data.mongodb</code> configuration).
     */
    public static class Mongodb {
        /**
         * Should not be set when host is set.
         */
        private String uri;

        /**
         * Should not be set when URI is set.
         */
        private String host;

        /**
         * Should not be set when URI is set.
         */
        private Integer port;

        /**
         * Should not be set when URI is set.
         */
        private String database;

        /**
         * Should not be set when URI is set.
         */
        private String authenticationDatabase;

        /**
         * Should not be set when URI is set.
         */
        private String username;

        /**
         * Should not be set when URI is set.
         */
        private char[] password;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public String getAuthenticationDatabase() {
            return authenticationDatabase;
        }

        public void setAuthenticationDatabase(String authenticationDatabase) {
            this.authenticationDatabase = authenticationDatabase;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public char[] getPassword() {
            return password;
        }

        public void setPassword(char[] password) {
            this.password = password;
        }
    }

    public boolean isDedicatedMongodbConfigurationEnabled() {
        return mongodb != null;
    }

    protected String defaultObjectAccessHook(){
        return DEFAULT_OBJECT_ACCESS_HOOK;
    }
}
