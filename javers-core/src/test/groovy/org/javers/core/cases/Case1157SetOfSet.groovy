package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.Diff
import org.javers.core.metamodel.annotation.ValueObject
import org.javers.core.metamodel.type.ManagedType
import org.javers.repository.jql.QueryBuilder
import org.junit.Test
import spock.lang.Specification

import javax.persistence.Id

/**
 * @author Maik Toepfer
 */
class Case1157SetOfSet extends Specification {

    static class A {
        Set<B> bs;
    }

    static class B {
        Set<C> cs;
    }

    static class C {
        String foo;

        C(String foo) {
            this.foo = foo
        }
    }

    @Test
    def "repeated comparison of semantically identical objects should always result in no difference"() {
        given:
        Javers javers = JaversBuilder.javers().build()

        // object "a1" and "a2" are semantically identical
        B b1 = new B()
        b1.cs = [new C("a"), new C("b")] as Set
        A a1 = new A()
        a1.bs = [b1] as Set

        B b2 = new B()
        b2.cs = [new C("a"), new C("b")] as Set
        A a2 = new A()
        a2.bs = [b2] as Set

         when:
         def detectedDiffs = 0
         100.times {
             def diff = javers.compare(a1, a2)
             if (diff.hasChanges()) detectedDiffs++
         }

        then:
        detectedDiffs == 0
    }
}
