package org.javers.spring.boot.mongo;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.Collections;
import java.util.List;

import static com.mongodb.MongoClientOptions.builder;

/**
 * Helper class for creating {@code MongoClient} based on Javers MongoDB properties.
 * @see {@linkplain org.springframework.boot.autoconfigure.mongo.MongoClientFactory}
 */
public class JaversMongoHelper {

    private JaversMongoHelper(){}

    public static MongoClient createJaversMongoClient(JaversMongoProperties properties
            , MongoClientOptions options) {

        if(properties.getMongodb().getUri()!= null) {
            return createMongoClient(properties.getMongodb().getUri(), options);
        }
        if (hasCustomAddress(properties) || hasCustomCredentials(properties)) {
            if (options == null) {
                options = MongoClientOptions.builder().build();
            }
            MongoCredential credentials = getCredentials(properties);
            String host = properties.getMongodb().getHost()!= null ? properties.getMongodb().getHost()
                    : JaversMongoProperties.Mongodb.DEFAULT_HOST;
            int port = properties.getMongodb().getPort()!= null ? properties.getMongodb().getPort()
                    : JaversMongoProperties.Mongodb.DEFAULT_PORT;
            List<ServerAddress> seeds = Collections
                    .singletonList(new ServerAddress(host, port));
            return (credentials != null) ? new MongoClient(seeds, credentials, options)
                    : new MongoClient(seeds, options);
        }

        return null;
    }

    private static MongoClient createMongoClient(String uri, MongoClientOptions options) {
        return new MongoClient(new MongoClientURI(uri, builder(options)));
    }

    private static boolean hasCustomAddress(JaversMongoProperties properties) {
        return properties.getMongodb().getHost() != null || properties.getMongodb().getPort() != null;
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
        String database = properties.getMongodb().getAuthenticationDatabase()!= null
                ? properties.getMongodb().getAuthenticationDatabase() : properties.getMongodb().getMongoClientDatabase();
        char[] password = properties.getMongodb().getPassword();
        return MongoCredential.createCredential(username, database, password);
    }

}
