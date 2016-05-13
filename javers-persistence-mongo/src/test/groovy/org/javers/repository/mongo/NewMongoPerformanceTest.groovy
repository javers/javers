package org.javers.repository.mongo

import com.mongodb.MongoClient
import org.javers.repository.jql.NewPerformanceTest
import spock.lang.Ignore

import static org.javers.core.JaversBuilder.javers

@Ignore
class NewMongoPerformanceTest extends NewPerformanceTest {

    def setup() {
        def mongoRepository = new MongoRepository(new MongoClient().getDatabase("j_int_test"))
        javers = javers().registerJaversRepository(mongoRepository).build()
    }
}
