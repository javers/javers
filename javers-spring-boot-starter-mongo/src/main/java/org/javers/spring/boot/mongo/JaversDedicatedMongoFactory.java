package org.javers.spring.boot.mongo;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;

import javax.annotation.Nullable;
import java.util.Optional;

import static com.mongodb.MongoClientSettings.builder;

/**
 * Helper class for creating {@code MongoClient} based on Javers MongoDB properties.
 *
 * @see {@linkplain org.springframework.boot.autoconfigure.mongo.MongoClientFactory}
 */
class JaversDedicatedMongoFactory {
    private static String DEFAULT_HOST = "localhost";
    private static int DEFAULT_PORT = 27017;

    static MongoDatabase createMongoDatabase(JaversMongoProperties properties, @Nullable MongoClientSettings mongoClientSettings) {
        final MongoClientSettings settings = Optional.ofNullable(mongoClientSettings)
                .orElseGet(() -> builder().build());

        if (properties.getMongodb().getUri() != null) {
            final ConnectionString connectionString = new ConnectionString(properties.getMongodb().getUri());
            MongoClient mongoClient = MongoClients.create(connectionString);
            return mongoClient.getDatabase(connectionString.getDatabase());
        }
        if (properties.getMongodb().getHost() != null) {
            String host = properties.getMongodb().getHost() == null ? DEFAULT_HOST
                    : properties.getMongodb().getHost();
            int port = properties.getMongodb().getPort() == null ? DEFAULT_PORT
                    : properties.getMongodb().getPort();
            ServerAddress serverAddress = new ServerAddress(host, port);

            MongoCredential credentials = getCredentials(properties);
            MongoClient mongoClient;
            if (credentials != null)
                mongoClient = MongoClients.create(builder(settings).credential(credentials).build());
            else
                mongoClient = MongoClients.create(builder(settings).applyConnectionString(new ConnectionString(serverAddress.toString())).build());
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
