package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.junit.Test
import spock.lang.Specification

/**
 * @author Maik Toepfer
 */
class Case1157SetOfSet extends Specification {

    class ValueObject {
        Set<SetHolder> setWithSets
    }

    class SetHolder {
        Set<C> setWithC
    }

    class C {
        String foo

        C(String foo) {
            this.foo = foo
        }
    }

    @Test
    def "repeated comparison of semantically identical objects should always result in no difference"() {
        given:
        Javers javers = JaversBuilder.javers().build()

        // object "a1" and "a2" are semantically identical
        SetHolder b1 = new SetHolder()
        b1.setWithC = [new C("a"), new C("b")] as Set
        ValueObject left = new ValueObject()
        left.setWithSets = [b1] as Set

        SetHolder b2 = new SetHolder()
        b2.setWithC = [new C("a"), new C("b")] as Set
        ValueObject right = new ValueObject()
        right.setWithSets = [b2] as Set

         when:
         def detectedDiffs = 0
         50.times {
             def diff = javers.compare(left, right)
             if (diff.hasChanges()) detectedDiffs++
         }

        then:
        detectedDiffs == 0
    }
}
