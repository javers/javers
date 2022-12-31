package org.javers.repository.mongo.cases


import com.mongodb.client.MongoDatabase
import groovy.transform.EqualsAndHashCode
import org.javers.core.JaversBuilder
import org.javers.repository.mongo.MongoRepository
import org.javers.repository.mongo.BaseMongoTest

import javax.persistence.Id

import static org.javers.repository.jql.QueryBuilder.byInstanceId

/**
 * Map Dot Replacer Test
 *
 * @author luca010
 */
class MapKeyDotReplacerTest extends BaseMongoTest {

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
