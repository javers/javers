package org.javers.repository.mongo

import com.mongodb.client.MongoClient
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

@Testcontainers
abstract class BaseMongoTest extends Specification {
    @Shared
    static DockerizedMongoContainer dockerizedMongoContainer = new DockerizedMongoContainer()

    @Shared
    static MongoClient mongoClient = dockerizedMongoContainer.mongoClient

}