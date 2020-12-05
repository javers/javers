package org.javers.spring.boot.mongo;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.javers.common.collections.Lists;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;

import java.util.Optional;

/**
 * Helper class for creating {@code MongoClient} based on Javers MongoDB properties.
 *
 * @see {@linkplain org.springframework.boot.autoconfigure.mongo.MongoClientFactory}
 */
class JaversDedicatedMongoFactory {
    private static String DEFAULT_HOST = "localhost";
    private static int DEFAULT_PORT = 27017;

    static MongoDatabase createMongoDatabase(JaversMongoProperties properties,
                                             Optional<MongoClientSettings> mongoClientSettings) {
        if (properties.getMongodb().getUri() != null) {
            ConnectionString connectionString = new ConnectionString(properties.getMongodb().getUri());
            MongoClient mongoClient = MongoClients.create(connectionString);
            return mongoClient.getDatabase(connectionString.getDatabase());
        }
        if (properties.getMongodb().getHost() != null) {
            String host = properties.getMongodb().getHost() == null ? DEFAULT_HOST
                    : properties.getMongodb().getHost();
            int port = properties.getMongodb().getPort() == null ? DEFAULT_PORT
                    : properties.getMongodb().getPort();

            Builder clientBuilder = mongoClientSettings
                    .map(s -> MongoClientSettings.builder(s))
                    .orElse(MongoClientSettings.builder());

            clientBuilder.applyToClusterSettings(b -> b.hosts(Lists.asList(new ServerAddress(host, port))));

            MongoCredential credentials = getCredentials(properties);
            if (credentials != null) {
                clientBuilder.credential(credentials);
            }

            MongoClient mongoClient = MongoClients.create(clientBuilder.build());
            return mongoClient.getDatabase(properties.getMongodb().getDatabase());
        }

        throw new JaversException(JaversExceptionCode.MALFORMED_JAVERS_MONGODB_PROPERTIES);
    }

    private static boolean hasCustomCredentials(JaversMongoProperties properties) {
        return properties.getMongodb().getUsername() != null
                && properties.getMongodb().getPassword() != null;
    }

    private static MongoCredential getCredentials(JaversMongoProperties properties) {
        if (!hasCustomCredentials(properties)) {
            return null;
        }
        String username = properties.getMongodb().getUsername();
        String database = properties.getMongodb().getAuthenticationDatabase() != null
                ? properties.getMongodb().getAuthenticationDatabase() : properties.getMongodb().getDatabase();
        char[] password = properties.getMongodb().getPassword();
        return MongoCredential.createCredential(username, database, password);
    }

}
