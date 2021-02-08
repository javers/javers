package org.javers.repository.mongo.cases

import com.mongodb.client.MongoClient

import static org.javers.repository.jql.QueryBuilder.byInstanceId

import javax.persistence.Id

import org.javers.core.JaversBuilder
import org.javers.repository.mongo.EmbeddedMongoFactory
import org.javers.repository.mongo.MongoRepository

import com.mongodb.client.MongoDatabase

import groovy.transform.EqualsAndHashCode
import spock.lang.Shared
import spock.lang.Specification

/**
 * Map Dot Replacer Test
 *
 * @author luca010
 */
class MapKeyDotReplacerTest extends Specification {

	@Shared def embeddedMongo = EmbeddedMongoFactory.create()
	@Shared MongoClient mongoClient

	def setupSpec() {
		mongoClient = embeddedMongo.getClient()
	}

	void cleanupSpec() {
		embeddedMongo.stop()
	}

	@EqualsAndHashCode
	class SampleDoc {
		@Id
		int id = 1

		Map<String, Integer> state
	}

	def "should commit and read snapshot of Entity containing state field with dot keys"() {
		given:
		MongoDatabase mongo = mongoClient.getDatabase("test")

		def javers = JaversBuilder.javers()
				.registerJaversRepository(new MongoRepository(mongo))
				.build()
		def cdo = new SampleDoc(id: 1, state: ['key.test1': 1, 'key.test2': 2])

		when:
		javers.commit('author', cdo)
		def snapshots = javers.findSnapshots(byInstanceId(1, SampleDoc).build())

		then:
		snapshots[0].getPropertyValue('state') == ['key.test1': 1, 'key.test2': 2]
	}
}
