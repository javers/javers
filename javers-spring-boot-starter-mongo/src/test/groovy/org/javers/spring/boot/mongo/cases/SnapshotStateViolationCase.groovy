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

	enum SimpleEnum {
		ENUM1, ENUM2
	}

	interface ISimplePojo {
		SimpleEnum getName()

		void setName(SimpleEnum name)
	}

	class SimplePojo implements ISimplePojo {
		public SimpleEnum name;

		SimpleEnum getName() {
			return name
		}

		void setName(SimpleEnum name) {
			this.name = name
		}
	}

	 interface IExtendedPojo extends ISimplePojo {
		SimpleEnum getAnotherName()

		void setAnotherName(SimpleEnum anotherName)
	}

	class ExtendedPojo extends SimplePojo implements IExtendedPojo {
		private SimpleEnum anotherName

		SimpleEnum getAnotherName() {
			return anotherName
		}

		void setAnotherName(SimpleEnum anotherName) {
			this.anotherName = anotherName
		}
	}

	def "should expect not to get snapshot violation exception when mappingStyle: bean" () {
		given:
		javersProperties.mappingStyle == "bean"

		ExtendedPojo ep = new ExtendedPojo()
		ep.setAnotherName(SimpleEnum.ENUM1)
		ep.setName(SimpleEnum.ENUM2)

		when:
		javers.commit("a", ep)
		def snapshots = javers.findSnapshots(QueryBuilder.byInstance(ep).build())
		
		then:
		assert snapshots.size() == 1
	}
}
