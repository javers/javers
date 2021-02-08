package org.javers.core.examples

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import spock.lang.Specification

class CompositeIdExample extends Specification {

    class PersonWithCompositeId {
      @Id String name
      @Id String surname
      String data
    }

    def "should support Entity with Composite-Id"(){
      given:
      def javers = JaversBuilder.javers().build()

      def first = new PersonWithCompositeId(name:    'Elizabeth',
                                            surname: 'Batory',
                                            data:    'some data')
      def second= new PersonWithCompositeId(name:    'Elizabeth',
                                            surname: 'Batory',
                                            data:    'more data')
      javers.commit('author', first)
      javers.commit('author', second)

      when:
      Map localId = [
              name:    'Elizabeth',
              surname: 'Batory'
      ]

      def snapshot = javers.getLatestSnapshot(localId, PersonWithCompositeId).get()

      then:
      snapshot.globalId.value().endsWith('PersonWithCompositeId/Elizabeth,Batory')
      snapshot.globalId.cdoId instanceof String
      snapshot.globalId.cdoId == 'Elizabeth,Batory'
      snapshot.getPropertyValue('name') == 'Elizabeth'
      snapshot.getPropertyValue('surname') == 'Batory'
      snapshot.getPropertyValue('data') == 'more data'

      println snapshot
    }

    class NameAndSurname {
        String name
        String surname
    }

    class PersonWithValueObjectId {
        @Id NameAndSurname name
        String data
    }

    def "should support Entity with ValueObject-Id"(){
        given:
        def javers = JaversBuilder.javers().build()

        def first = new PersonWithValueObjectId(
                name: new NameAndSurname(name: 'Elizabeth', surname: 'Batory'),
                data: 'some data')
        def second= new PersonWithValueObjectId(
                name: new NameAndSurname(name: 'Elizabeth', surname: 'Batory'),
                data: 'more data')

        javers.commit('author', first)
        javers.commit('author', second)

        when:
        def localId = new NameAndSurname(name: 'Elizabeth', surname: 'Batory')

        def snapshot = javers.getLatestSnapshot(localId, PersonWithValueObjectId).get()

        then:

        snapshot.globalId.value().endsWith('PersonWithValueObjectId/Elizabeth,Batory')
        snapshot.globalId.cdoId instanceof NameAndSurname
        snapshot.getPropertyValue('name') instanceof NameAndSurname
        snapshot.getPropertyValue('name').name == 'Elizabeth'
        snapshot.getPropertyValue('name').surname == 'Batory'
        snapshot.getPropertyValue('data') == 'more data'

        println snapshot
    }
}
