package org.javers.repository.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.testcontainers.mongodb.MongoDBContainer

class DockerizedMongoContainer {
    MongoDBContainer mongoDBContainer
    MongoClient mongoClient

    DockerizedMongoContainer() {
        this.mongoDBContainer = startMongo()
        this.mongoClient = MongoClients.create(mongoDBContainer.replicaSetUrl)
    }

    DockerizedMongoContainer(MongoClientSettings.Builder mongoClientSettingsBuilder) {
        this.mongoDBContainer = startMongo()
        this.mongoClient = MongoClients.create(
                mongoClientSettingsBuilder
                        .applyConnectionString(new ConnectionString(mongoDBContainer.replicaSetUrl)).build())
    }

    MongoDBContainer startMongo() {
        println ("starting MongoDB container ...")
        def mongoDBContainer = new MongoDBContainer("mongo:8.2")
        mongoDBContainer.start()
        mongoDBContainer
    }

    int getMongoPort() {
        mongoDBContainer.getMappedPort(27017)
    }

    String getReplicaSetUrl() {
        mongoDBContainer.getReplicaSetUrl()
    }

    MongoClient getMongoClient() {
        return mongoClient
    }
}
