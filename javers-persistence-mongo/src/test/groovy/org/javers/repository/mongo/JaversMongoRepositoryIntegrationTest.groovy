package org.javers.repository.mongo

import com.github.fakemongo.Fongo
import com.mongodb.Mongo
import org.javers.core.Javers
import org.javers.core.JaversRepositoryIntegrationTest
import org.javers.core.commit.CommitAssert
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.model.SnapshotEntity
import org.javers.core.snapshot.SnapshotsAssert
import org.javers.repository.api.JaversRepository
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.metamodel.object.InstanceId.InstanceIdDTO.instanceId
import static org.javers.core.metamodel.object.ValueObjectId.ValueObjectIdDTO.valueObjectId
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class JaversMongoRepositoryIntegrationTest extends JaversRepositoryIntegrationTest {

    @Override
    def setup() {
        def db = new Fongo("myDb").mongo.getDB("test")
        def mongoRepository = new MongoRepository(db)
        javers = javers().registerJaversRepository(mongoRepository).build()

        mongoRepository.setJsonConverter(javers.getJsonConverter());
    }
}
