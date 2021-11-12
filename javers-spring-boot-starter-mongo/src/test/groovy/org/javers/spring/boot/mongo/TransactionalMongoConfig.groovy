package org.javers.spring.boot.mongo

import com.github.silaev.mongodb.replicaset.MongoDbReplicaSet
import com.mongodb.client.MongoClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory

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
}
