package org.javers.spring.boot.mongo

import com.github.silaev.mongodb.replicaset.MongoDbReplicaSet
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.WriteConcern
import com.mongodb.client.MongoClient
import org.jetbrains.annotations.NotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory

import java.util.concurrent.TimeUnit

@Configuration
@Profile("TransactionalMongo")
class TransactionalMongoConfig {

    @Autowired
    private JaversMongoProperties javersMongoProperties

    @Bean(destroyMethod = "stop")
    MongoDbReplicaSet mongoDbReplicaSet() {
        def replicaSet = MongoDbReplicaSet.builder()
                .replicaSetNumber(1)
                .mongoDockerImageName("mongo:4.4.4")
                .build()
        replicaSet.start()
        replicaSet
    }

    @Bean MongoClient mongoClient (MongoDbReplicaSet replicaSet) {
        def mongoRsUrl = replicaSet.getReplicaSetUrl()

        println("mongoRsUrl: " + mongoRsUrl)
        def mongoSyncClient = com.mongodb.client.MongoClients.create(
                ConnectionUtils.getMongoClientSettingsWithTimeout(mongoRsUrl)
        )
        mongoSyncClient
    }

    @Bean
    MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        println("my mongoClient: "+ mongoClient)
        new SimpleMongoClientDatabaseFactory(mongoClient, "test")
    }

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        println("my mongoDatabaseFactory: "+ mongoDatabaseFactory)
        def t = new MongoTransactionManager(mongoDatabaseFactory)
        println("my mongoTransactionManager: "+ t)
        t
    }

    /**
     * @author Konstantin Silaev on 3/19/2020
     */
    class ConnectionUtils {

        /**
         * Used for setting timeouts to fail-fast behaviour.
         */
        static MongoClientSettings getMongoClientSettingsWithTimeout(
                final String mongoRsUrlPrimary,
                final WriteConcern writeConcern,
                final int timeout
        ) {
            final ConnectionString connectionString = new ConnectionString(mongoRsUrlPrimary)
            return MongoClientSettings.builder()
                    .writeConcern(writeConcern.withWTimeout(timeout, TimeUnit.SECONDS))
                    .applyToClusterSettings(c -> c.serverSelectionTimeout(timeout, TimeUnit.SECONDS))
                    .applyConnectionString(connectionString)
                    .applyToSocketSettings(
                            b -> b
                                    .readTimeout(timeout, TimeUnit.SECONDS)
                                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    ).build()
        }

        static MongoClientSettings getMongoClientSettingsWithTimeout(
                final String mongoRsUrlPrimary
        ) {
            return getMongoClientSettingsWithTimeout(mongoRsUrlPrimary, WriteConcern.W1, 5)
        }
    }
}
