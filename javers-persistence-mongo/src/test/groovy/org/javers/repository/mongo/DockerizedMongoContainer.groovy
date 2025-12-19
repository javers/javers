package org.javers.repository.mongo

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

    MongoDBContainer startMongo() {
        println ("starting MongoDB container ...")
        def mongoDBContainer = new MongoDBContainer("mongo:4.4.4")
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
