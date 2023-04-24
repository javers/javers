package org.javers.spring.boot.mongo

import com.mongodb.client.MongoClient
import org.javers.core.CommitIdGenerator
import org.javers.core.Javers
import org.javers.core.MappingStyle
import org.javers.core.diff.ListCompareAlgorithm
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author pawelszymczyk
 */
class JaversMongoStarterDefaultsTest extends BaseSpecification{
    static String DB_NAME = 'spring-mongo-default'

    @Autowired Javers javers

    @Autowired
    private MongoClient mongoClient; //from spring-boot-starter-data-mongodb

    @Autowired
    JaversMongoProperties javersProperties

    def setup () {
         mongoClient.getDatabase(DB_NAME).getCollection("jv_snapshots").drop()
    }

    def "should provide default configuration"() {
        expect:
        javers.coreConfiguration.listCompareAlgorithm == ListCompareAlgorithm.SIMPLE
        javers.coreConfiguration.mappingStyle == MappingStyle.FIELD
        javers.coreConfiguration.initialChanges
        javers.coreConfiguration.terminalChanges
        javers.coreConfiguration.prettyPrint
        javers.coreConfiguration.commitIdGenerator == CommitIdGenerator.SYNCHRONIZED_SEQUENCE
        javers.coreConfiguration.usePrimitiveDefaults

        javersProperties.auditableAspectEnabled
        javersProperties.springDataAuditableRepositoryAspectEnabled
       !javersProperties.isTypeSafeValues()
        javersProperties.packagesToScan == ""
       !javersProperties.documentDbCompatibilityEnabled
        javersProperties.objectAccessHook == "org.javers.spring.mongodb.DBRefUnproxyObjectAccessHook"
       !javersProperties.mongodb
        javersProperties.snapshotsCacheSize == 5000
        javersProperties.schemaManagementEnabled
        javersProperties.snapshotCollectionName == null
        javersProperties.headCollectionName == null
    }

    def "should connect to Mongo configured in spring.data.mongodb properties"(){
      when:
      def dummyEntity = new DummyEntity(UUID.randomUUID().hashCode())
      javers.commit("a", dummyEntity)
      def snapshots = javers.findSnapshots(QueryBuilder.byInstance(dummyEntity).build())

      then:
      javers.repository.delegate.mongoSchemaManager.mongo.name == "spring-mongo-default"
      snapshots.size() == 1

      mongoClient.getDatabase(DB_NAME).getCollection("jv_snapshots").countDocuments() == 1
    }
}
