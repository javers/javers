package org.javers.spring.boot.mongo

import com.mongodb.client.MongoClient
import org.javers.core.CommitIdGenerator
import org.javers.core.Javers
import org.javers.core.MappingStyle
import org.javers.core.diff.ListCompareAlgorithm
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author bbrakefieldmn
 */
class JaversMongoStarterNullDoubleTest extends BaseSpecification{
    static String DB_NAME = 'spring-mongo-default'

    @Autowired Javers javers

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    JaversMongoProperties javersProperties

    def setup () {
         mongoClient.getDatabase(DB_NAME).getCollection("jv_snapshots").drop()
    }


    def "should persist entity with Double.NaN value to Mongo"(){
      when:
      def dummyEntity = new DummyEntityWithDouble(UUID.randomUUID().hashCode(), Double.NaN)
      javers.commit("a", dummyEntity)
      def snapshots = javers.findSnapshots(QueryBuilder.byInstance(dummyEntity).build())

      then:
      javers.repository.delegate.mongoSchemaManager.mongo.name == "spring-mongo-default"
      snapshots.size() == 1

      mongoClient.getDatabase(DB_NAME).getCollection("jv_snapshots").countDocuments() == 1
    }


}
