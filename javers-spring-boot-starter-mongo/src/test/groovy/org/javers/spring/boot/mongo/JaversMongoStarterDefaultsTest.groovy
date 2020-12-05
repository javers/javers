package org.javers.spring.boot.mongo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

/**
 * @author pawelszymczyk
 */
@SpringBootTest(classes = [TestApplication])
class JaversMongoStarterDefaultsTest extends Specification{
    static String DB_NAME = 'spring-mongo-default'

    @Autowired Javers javers

    @Autowired
    private MongoClient mongoClient; //from spring-boot-starter-data-mongodb

    @Autowired
    JaversMongoProperties javersProperties

    def "should provide default configuration"() {
        expect:
        javersProperties.algorithm == "simple"
        javersProperties.mappingStyle == "field"
       !javersProperties.newObjectSnapshot
        javersProperties.prettyPrint
       !javersProperties.typeSafeValues
        javersProperties.commitIdGenerator == "synchronized_sequence"
       !javersProperties.documentDbCompatibilityEnabled
        javersProperties.auditableAspectEnabled
        javersProperties.springDataAuditableRepositoryAspectEnabled
        javersProperties.packagesToScan == ""
       !javersProperties.mongodb
        javersProperties.objectAccessHook == "org.javers.spring.mongodb.DBRefUnproxyObjectAccessHook"
        javersProperties.snapshotsCacheSize == 5000
    }

    def "should connect to Mongo configured in spring.data.mongodb properties"(){
      when:
      def dummyEntity = new DummyEntity(UUID.randomUUID().hashCode())
      javers.commit("a", dummyEntity)
      def snapshots = javers.findSnapshots(QueryBuilder.byInstance(dummyEntity).build())

      MongoDatabase mongoDatabase = mongoClient.getDatabase(DB_NAME)

      then:
      javers.repository.delegate.mongoSchemaManager.mongo.name == "spring-mongo-default"
      snapshots.size() == 1
      mongoDatabase.getCollection("jv_snapshots").countDocuments() == 1
    }
}
