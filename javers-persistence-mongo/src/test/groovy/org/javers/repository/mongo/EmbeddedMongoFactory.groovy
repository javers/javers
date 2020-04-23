package org.javers.repository.mongo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodProcess
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network

class EmbeddedMongoFactory {

    static MongodStarter starter = MongodStarter.getDefaultInstance()
    static final String BIND_IP = "localhost"
    static int PORT = 12345

    static class EmbeddedMongo {
        private MongodExecutable mongodExecutable
        private MongoClient mongoClient

        EmbeddedMongo (int port) {
            mongodExecutable = starter.prepare(new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(BIND_IP, port, Network.localhostIsIPv6()))
                    .build())
            mongodExecutable.start()
            mongoClient = MongoClients.create("mongodb://$BIND_IP:$port")
        }

        MongoClient getClient () {
            mongoClient
        }

        void stop () {
            if (mongodExecutable != null) {
                mongodExecutable.stop()
            }
        }
    }

    static EmbeddedMongo create(int port = PORT) {
        return new EmbeddedMongo(port)
    }
}
