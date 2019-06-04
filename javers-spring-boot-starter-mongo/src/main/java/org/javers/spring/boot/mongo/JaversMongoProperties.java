package org.javers.spring.boot.mongo;

import com.mongodb.MongoClientURI;
import org.javers.spring.JaversSpringProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author pawelszymczyk
 */
@ConfigurationProperties(prefix = "javers")
public class JaversMongoProperties extends JaversSpringProperties {

    private boolean documentDbCompatibilityEnabled = false;

    private Mongodb mongodb = new Mongodb();

    public boolean isDocumentDbCompatibilityEnabled() {
        return documentDbCompatibilityEnabled;
    }

    public void setDocumentDbCompatibilityEnabled(boolean documentDbCompatibilityEnabled) {
        this.documentDbCompatibilityEnabled = documentDbCompatibilityEnabled;
    }

    public Mongodb getMongodb() {
        return mongodb;
    }

    public void setMongodb(Mongodb mongodb) {
        this.mongodb = mongodb;
    }

    /**
     * Override the spring-boot-starter-data-mongodb
     * configuration and use the Javers defined properties.
     */
    public static class Mongodb {

        /**
         * Default port used when the configured port is {@code null}.
         */
        public static final int DEFAULT_PORT = 27017;

        /**
         * Default URI used when the configured URI is {@code null}.
         */
        public static final String DEFAULT_URI = "mongodb://localhost/test";

        /**
         * Default host used when the configured port is {@code null}.
         */
        public static final String DEFAULT_HOST = "localhost";

        /**
         * Mongo server host. Cannot be set with URI.
         * If not set points to {@literal localhost}
         */
        private String host = DEFAULT_HOST;

        /**
         * Mongo server port. Cannot be set with URI.
         * If not set value {@literal 27017}
         */
        private Integer port = DEFAULT_PORT;

        /**
         * Mongo database URI. Cannot be set with host, port and credentials.
         */
        private String uri;

        /**
         * Database name.
         */
        private String database;

        /**
         * Authentication database name.
         */
        private String authenticationDatabase;

        /**
         * Login user of the mongo server. Cannot be set with URI.
         */
        private String username;

        /**
         * Login password of the mongo server. Cannot be set with URI.
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

        public String getMongoClientDatabase() {
            if (this.database != null) {
                return this.database;
            }
            return new MongoClientURI(determineUri()).getDatabase();
        }

        private String determineUri() {
            return (this.uri != null) ? this.uri : DEFAULT_URI;
        }
    }

    public boolean javersMongodbEnabled() {
        if(getMongodb().getDatabase()!= null || getMongodb().getUri()!= null) {
            return true;
        }
        return false;
    }
}
