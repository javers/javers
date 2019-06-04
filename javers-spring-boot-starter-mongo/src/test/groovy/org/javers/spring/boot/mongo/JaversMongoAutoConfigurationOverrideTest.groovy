package org.javers.spring.boot.mongo

import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.IMongoCmdOptions
import de.flapdoodle.embed.mongo.config.IMongodConfig
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("javers-mongo")
class JaversMongoAutoConfigurationOverrideTest extends Specification {

    @Shared
    MongodExecutable mongodExecutable

    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    /**
     * The embedded MongoDB authentication is not enabled
     * When connecting to a real MongoDB database
     * authentication can be enabled.
     * @see {@code JaversMongoProperties}
     */
    def setupSpec() {
        String ip = "localhost"
        int port = 32001
        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(new Net(ip, port, Network.localhostIsIPv6()))
                .build()

        MongodStarter starter = MongodStarter.getDefaultInstance()
        mongodExecutable = starter.prepare(mongodConfig)
        mongodExecutable.start()
    }

    def "should build javers instance overriding the spring-boot-data-mongodb configuration"() {
        when:
        def dummyEntity = dummyEntityRepository.save(new DummyEntity(UUID.randomUUID().hashCode()))
        def snapshots = javers
                .findSnapshots(QueryBuilder.byInstanceId(dummyEntity.id, DummyEntity).build())

        then:
        assert snapshots.size() == 1
        assert snapshots[0].commitMetadata.properties["key"] == "ok"
        assert snapshots[0].commitMetadata.author == "unauthenticated"
    }

    def cleanupSpec() {
        if(mongodExecutable!= null) {
            mongodExecutable.stop()
        }
    }
}
