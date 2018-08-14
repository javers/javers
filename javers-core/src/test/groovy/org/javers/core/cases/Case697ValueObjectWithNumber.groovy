package org.javers.core.cases

import org.javers.core.JaversBuilder
import spock.lang.Specification

class Case697ValueObjectWithNumber extends Specification {
    class NumberTest {
        Number testNumber

        NumberTest(Number testNumber) {
            this.testNumber = testNumber
        }
    }

    def "should compare Value Objects with Numbers"(){
      given:
      def javers = JaversBuilder.javers().build()

      def number1 = new NumberTest(23)
      def number2 = new NumberTest(20)

      when:
      def diff = javers.compare(number1, number2)

      then:
      diff.changes[0].left == 23
      diff.changes[0].right == 20
    }
}
