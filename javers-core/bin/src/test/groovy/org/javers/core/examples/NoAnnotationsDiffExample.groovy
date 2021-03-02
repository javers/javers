package org.javers.core.examples

import groovy.transform.TupleConstructor
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.clazz.EntityDefinitionBuilder
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class NoAnnotationsDiffExample extends Specification {

    @TupleConstructor
    class LegacyPerson {
        int id
        String lastName
        String someTechnicalField
    }

    def 'should use types configured in JaversBuilder when doing diff '(){
      given:

      def personEntityDefinition = EntityDefinitionBuilder
              .entityDefinition(LegacyPerson.class)
              .withIdPropertyName('id')
              .withIgnoredProperties(['someTechnicalField'])
              .build()

      def javers = new JaversBuilder()
              .registerEntity(personEntityDefinition)
              .build()

      when: 'diff example'
      def oldVer = new LegacyPerson(1, 'Bob', "abc")
      def newVer = new LegacyPerson(1, 'Uncle Bob', "xyx")

      def diff = javers.compare(oldVer, newVer)

      then:
      diff.changes.size() == 1
      diff.changes[0].propertyName == 'lastName'
    }
}
