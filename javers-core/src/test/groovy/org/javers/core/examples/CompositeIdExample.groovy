package org.javers.core.examples

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.object.InstanceId
import spock.lang.Specification

import javax.persistence.IdClass

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

    class Person {
        @Id String email;
        String name;
    }

    class Order {
        @Id String itemId;
        String description;
    }

    @IdClass(ShipmentWithCompositeId.ShipmentIdClass.class)
    class ShipmentWithCompositeId {
        @Id Person person
        @Id Order order
        String address

        class ShipmentIdClass {
            String person
            String order
        }
    }

    def "should support Entity with Composite-Id and IdClass"(){
        given:
        def javers = JaversBuilder.javers().build()

        def first = new ShipmentWithCompositeId(
                person: new Person(email: "someone@localhost", name: "Some One"),
                order: new Order(itemId: "id-123134", description: "Awesome Item"),
                address: "Home"
        )
        javers.commit('author', first)

        when:
        Map localId = [
                person: new Person(email: "someone@localhost"),
                order:  new Order(itemId: "id-123134")
        ]

        def snapshot = javers.getLatestSnapshot(localId, ShipmentWithCompositeId).get()

        then:
        snapshot.globalId.value().endsWith('ShipmentWithCompositeId/id-123134,someone@localhost')
        snapshot.globalId.cdoId instanceof String
        snapshot.globalId.cdoId == 'id-123134,someone@localhost'
        snapshot.getPropertyValue('person') instanceof InstanceId
        snapshot.getPropertyValue('person').cdoId == 'someone@localhost'
        snapshot.getPropertyValue('order') instanceof InstanceId
        snapshot.getPropertyValue('order').cdoId == 'id-123134'
        snapshot.getPropertyValue('address') == 'Home'

        println snapshot
    }
}
