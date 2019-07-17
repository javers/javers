package org.javers.spring.boot.mongo;

import org.javers.core.Javers
import org.javers.spring.annotation.JaversSpringDataAuditable
import org.javers.spring.boot.mongo.DummyEntity
import org.javers.spring.boot.mongo.snap.SnapshotViolationPojoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.CrudRepository
import org.springframework.test.context.ActiveProfiles

import spock.lang.Specification

import org.javers.spring.boot.mongo.snap.ExtendedPojo
import org.javers.spring.boot.mongo.snap.SimpleEnum
import org.javers.repository.jql.QueryBuilder


@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
public class JaversSnapshotViolationTest extends Specification {

	@Autowired
	JaversMongoProperties javersProperties

	@Autowired
	Javers javers
	
	@Autowired
	SnapshotViolationPojoRepository mongorepo

	def "should expect not to get snapshot violation exception when mappingStyle: bean" () {
		expect:
		javersProperties.mappingStyle == "bean"
		
		when:		
		ExtendedPojo ep = new ExtendedPojo();
		ep.setAnotherName(SimpleEnum.ENUM1);
		ep.setName(SimpleEnum.ENUM2);
		
		mongorepo.save(ep);
		
		def snapshots = javers.findSnapshots(QueryBuilder.byInstance(ExtendedPojo).build())
		
		then:
		assert snapshots.size() == 1
	}
}
