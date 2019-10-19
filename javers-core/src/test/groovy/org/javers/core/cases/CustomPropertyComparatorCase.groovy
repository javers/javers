package org.javers.core.cases

import org.javers.common.collections.Lists
import org.javers.core.JaversBuilder
import org.javers.core.diff.ListCompareAlgorithm
import org.javers.core.diff.changetype.PropertyChangeMetadata
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.custom.CustomPropertyComparator
import org.javers.core.metamodel.property.Property
import spock.lang.Specification
import spock.lang.Unroll

/**
 * see https://stackoverflow.com/questions/53418466/using-custompropertycomparator-with-java-util-list
 */
class CustomPropertyComparatorCase extends Specification {

    class Person {
        private String name
        private String ignoreThis
    }

    class Company {
        private String id
        private Collection<Person> clients
        private List<Person> partners
    }

    class PersonComparator implements CustomPropertyComparator<Person, ValueChange> {
        Optional<ValueChange> compare(Person left, Person right, PropertyChangeMetadata metadata, Property property) {
            if (left.name.equals(right.name))
                return Optional.empty()
            return Optional.of(new ValueChange(metadata, left.name, right.name));
        }

        @Override
        boolean equals(Person left, Person right) {
            return left.name.equals(right.name)
        }

        @Override
        String toString(Person value) {
            return value.name
        }
    }

    @Unroll
    def "should use CustomPropertyComparator for raw Collection items with #alg"(){
      given:
      def javers = JaversBuilder.javers()
              .withListCompareAlgorithm(alg)
              .registerCustomComparator(new  PersonComparator(), Person).build()

      when:
      Company c1 = new Company(id: "1", clients: [new Person(name: "james", ignoreThis: "ignore this")])
      Company c2 = new Company(id: "1", clients: [new Person(name: "james")])
      def diff = javers.compare(c1, c2)

      then:
      diff.changes.size() == 0

      when:
      c1 = new Company(id: "1", clients: [new Person(name: "james")] )
      c2 = new Company(id: "1", clients: [new Person(name: "james"), new Person(name: "kaz")] )

      diff = javers.compare(c1, c2)

      then:
      diff.changes.size() == 1

      where:
      alg << Lists.asList(ListCompareAlgorithm.values())
    }

    @Unroll
    def "should use CustomPropertyComparator for List items with #alg"(){
        given:
        def javers = JaversBuilder.javers()
                .withListCompareAlgorithm(alg)
                .registerCustomComparator(new  PersonComparator(), Person).build()

        when:
        Company c1 = new Company(id: "1", partners: [new Person(name: "james", ignoreThis: "ignore this")])
        Company c2 = new Company(id: "1", partners: [new Person(name: "james")])

        def diff = javers.compare(c1, c2)

        then:
        diff.changes.size() == 0

        when:
        c1 = new Company(id: "1", partners: [new Person(name: "james")] )
        c2 = new Company(id: "1", partners: [new Person(name: "james"), new Person(name: "kaz")] )

        diff = javers.compare(c1, c2)

        then:
        diff.changes.size() == 1

        where:
        alg << [ListCompareAlgorithm.SIMPLE, ListCompareAlgorithm.LEVENSHTEIN_DISTANCE]
    }
}
