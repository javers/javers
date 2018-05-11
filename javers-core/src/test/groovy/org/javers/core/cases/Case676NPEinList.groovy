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
    }

    def "should support nulls in LIST "(){
      given:
      Customer customer = new Customer(id:"1", orders:[null, null])
      def javers = JaversBuilder.javers().build()

      when:
      javers.commit("a", customer)
      def shadow = javers.findShadows(QueryBuilder.byInstanceId("1", Customer))[0].get()

      then:
      println "Customer shadow : " + shadow
      shadow.id == "1"
    }
}
