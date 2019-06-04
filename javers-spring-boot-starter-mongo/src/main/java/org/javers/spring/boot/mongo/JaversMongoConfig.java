package org.javers.spring.boot.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@Configuration
@EnableConfigurationProperties({JaversMongoProperties.class})
public class JaversMongoConfig {
    private static final Logger logger = LoggerFactory.getLogger(JaversMongoConfig.class);

    private static final String JAVERS_MONGO_CLIENT_OPTIONS_BEAN_NAME = "javersMongoClientOptions";
    private static final String JAVERS_MONGO_DATABSE_BEAN_NAME = "javersMongoDatabase";

    private final JaversMongoProperties properties;

    private final MongoClientOptions mongoClientOptions;

    public JaversMongoConfig(JaversMongoProperties properties
            , @Qualifier(JAVERS_MONGO_CLIENT_OPTIONS_BEAN_NAME) @Nullable MongoClientOptions mongoClientOptions) {
        this.properties = properties;
        this.mongoClientOptions = mongoClientOptions;
    }

    /**
     *
     * @param client {@code MongoClient} created by spring-boot-starter-data-mongodb.
     * @param mongoProperties {@code MongoProperties} created by spring-boot-starter-data-mongodb.
     * @return {@code MongoDatabase} instance from spring-boot-starter-data-mongodb or new instance defined by Javers.
     */
    @Bean(name = JAVERS_MONGO_DATABSE_BEAN_NAME)
    MongoDatabase mongoDatabase(MongoClient client, MongoProperties mongoProperties) {
        MongoDatabase db = null;
        if(!properties.javersMongodbEnabled()) {
            logger.info("Javers MongoDB configuration override is set to false. " +
                            "Connecting to spring-boot-starter-data-mongodb configured database: [ {} ]"
                    , mongoProperties.getMongoClientDatabase());
            db = client.getDatabase(mongoProperties.getMongoClientDatabase());
        } else{
            logger.info("Javers MongoDB configuration override is set to true. " +
                            "Connecting to Javers configured database: [ {} ]"
                    , properties.getMongodb().getMongoClientDatabase());
            MongoClient jmc = JaversMongoHelper.createJaversMongoClient(properties
                    , mongoClientOptions!= null ? mongoClientOptions : MongoClientOptions.builder().build()); //mongo client options to be overridden by the application
            db = jmc.getDatabase(properties.getMongodb().getMongoClientDatabase());
        }

        return db;
    }
}
