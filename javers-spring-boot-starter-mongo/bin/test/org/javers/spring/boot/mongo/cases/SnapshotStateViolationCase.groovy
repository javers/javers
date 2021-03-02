package org.javers.spring.boot.mongo.cases

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.JaversSpringProperties
import org.javers.spring.boot.mongo.TestApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class SnapshotStateViolationCase extends Specification {

	@Autowired Javers javers

	@Autowired JaversSpringProperties javersProperties

	interface ISimplePojo {
		int getName()

		void setName(int name)
	}

	class SimplePojo implements ISimplePojo {
		int name;

		@Override
		int getName() {
			return name
		}

		@Override
		void setName(int name) {
			this.name = name
		}
	}

	 interface IExtendedPojo extends ISimplePojo {
		int getAnotherName()

		void setAnotherName(int anotherName)
	}

	class ExtendedPojo extends SimplePojo implements IExtendedPojo {
		int anotherName

		int getAnotherName() {
			return anotherName
		}

		void setAnotherName(int anotherName) {
			this.anotherName = anotherName
		}
	}

	def "should expect not to get snapshot violation exception when mappingStyle: bean" () {
		given:
		javersProperties.mappingStyle == "bean"

		ExtendedPojo ep = new ExtendedPojo()
		ep.setAnotherName(1)
		ep.setName(2)

		when:
		javers.commit("a", ep)
		def snapshots = javers.findSnapshots(QueryBuilder.byInstance(ep).build())
		
		then:
		assert snapshots.size() == 1
	}
}
