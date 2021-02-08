package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

import javax.persistence.Id

class Case676NPEinList extends Specification {

    class Customer {
        @Id
        String id
        List<Order> orders

        String toString() {
            "Customer{" +
                "id='" + id + '\'' +
                ", orders=" + orders +
            '}'
        }
    }

    class Order {
        String orderNumber

        String toString() {
            "Order{" +
                "orderNumber='" + orderNumber + '\'' +
            '}'
        }
    }

    def "should support nulls in List of Value Objects "(){
      given:
      Customer customer = new Customer(id:"1", orders:[null, new Order(orderNumber: "oo")])
      def javers = JaversBuilder.javers().build()

      when:
      javers.commit("a", customer)
      def snapshot = javers.findSnapshots(QueryBuilder.byInstanceId("1", Customer).build())[0]

      then:
      println "Customer snapshot : " + snapshot
      snapshot.getPropertyValue("orders")[0] == null
      snapshot.getPropertyValue("orders")[1].value().endsWith('$Customer/1#orders/0')
    }
}
