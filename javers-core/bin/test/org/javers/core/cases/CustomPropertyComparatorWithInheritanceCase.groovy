package org.javers.core.cases


import org.javers.core.JaversBuilder
import org.javers.core.diff.changetype.PropertyChangeMetadata
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.custom.CustomPropertyComparator
import org.javers.core.metamodel.object.GlobalId
import org.javers.core.metamodel.property.Property
import spock.lang.Specification

/**
 * @author konstantinkastanov* created on 2019-02-19
 */
class CustomPropertyComparatorWithInheritanceCase extends Specification {

	class MightyWizard {
		private IntValue level
	}

	class SimpleValue<T> {
		private T value

		SimpleValue(T value = null) {
			this.value = value
		}

		T getValue() {
			return value
		}

		void setValue(T value) {
			this.value = value
		}
	}

	class IntValue extends SimpleValue<Integer> {
		IntValue(Integer value = null) {
			super(value)
		}
	}

	class SimpleValueComparator implements CustomPropertyComparator<SimpleValue, ValueChange> {
		Optional<ValueChange> compare(SimpleValue left, SimpleValue right, PropertyChangeMetadata metadata, Property property) {
			if (left.getValue().equals(right.getValue()))
				return Optional.empty()
			return Optional.of(new ValueChange(metadata, left.getValue(), right.getValue()))
		}

		@Override
		boolean equals(SimpleValue left, SimpleValue right) {
			return left.getValue().equals(right.getValue())
		}

		@Override
		String toString(SimpleValue value) {
			return value.toString()
		}
	}

	def "should use CustomPropertyComparator for all subclasses"(){
		given:
		def javers = JaversBuilder.javers()
				.registerCustomComparator(new SimpleValueComparator(), SimpleValue).build()

		when:
		MightyWizard w1 = new MightyWizard();
		w1.level = new IntValue(1)
		MightyWizard w2 = new MightyWizard();
		w2.level = new IntValue(2)
		def diff = javers.compare(w1, w2)
		then:
		diff.changes.size() == 1
	}


}
