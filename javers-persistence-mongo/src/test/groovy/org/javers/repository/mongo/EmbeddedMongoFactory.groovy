package org.javers.repository.mongo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfig
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network

class EmbeddedMongo {
    static final String BIND_IP = "localhost"
    int port
    MongodExecutable mongodExecutable

    EmbeddedMongo(int port) {
        this.port = port
        MongodStarter starter = MongodStarter.getDefaultInstance();
        MongodConfig mongodConfig = MongodConfig.builder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(port, Network.localhostIsIPv6()))
                .build()

        mongodExecutable = starter.prepare(mongodConfig)
        mongodExecutable.start()
    }

    MongoClient getClient() {
        MongoClient mongo = MongoClients.create(String.format("mongodb://$BIND_IP:$port"))
        mongo
    }

    void stop() {
        if (mongodExecutable) {
            mongodExecutable.stop()
        }
    }
}

class EmbeddedMongoFactory {
    static int PORT = 12345

    static EmbeddedMongo create(int port = PORT) {
        new EmbeddedMongo(port)
    }
}
