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
        Set<SetHolder> bs
    }

    class SetHolder {
        Set<C> cs
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
        b1.cs = [new C("a"), new C("b")] as Set
        ValueObject left = new ValueObject()
        left.bs = [b1] as Set

        SetHolder b2 = new SetHolder()
        b2.cs = [new C("a"), new C("b")] as Set
        ValueObject right = new ValueObject()
        right.bs = [b2] as Set

         when:
         def detectedDiffs = 0
         100.times {
             def diff = javers.compare(left, right)
             println (it +"."+diff.prettyPrint())
             if (diff.hasChanges()) detectedDiffs++
         }

        then:
        detectedDiffs == 0
    }
}
