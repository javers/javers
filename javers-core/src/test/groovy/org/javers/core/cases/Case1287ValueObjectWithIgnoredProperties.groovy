package org.javers.core.cases

import jakarta.persistence.Id
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.DiffIgnoreProperties
import org.javers.core.metamodel.annotation.ValueObject
import org.javers.core.metamodel.type.ValueObjectType
import spock.lang.Specification

class Case1287ValueObjectWithIgnoredProperties extends Specification {

	@ValueObject
	@DiffIgnoreProperties("id")
	class MyValueObject {

		@Id
		private UUID id

		private String property
	}

	def "should recognize class as a value object type when @DiffIgnoreProperties is used"() {
		given:
		Javers javers = JaversBuilder.javers().build()

		expect:
		javers.getTypeMapping( MyValueObject ) instanceof ValueObjectType
		javers.getTypeMapping( MyValueObject ).properties.collect {it.name} == ['property']
	}
}
