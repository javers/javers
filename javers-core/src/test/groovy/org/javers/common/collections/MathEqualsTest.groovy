package org.javers.common.collections

import static org.javers.core.metamodel.type.TokenType.mathFriendlyEquals
import spock.lang.Specification

class MathEqualsTest extends Specification{

    def "should compare Ints and Longs mathematically" () {
        expect:
         mathFriendlyEquals(1,  1)
         mathFriendlyEquals(1L, 1L)
         mathFriendlyEquals(1,  1L)
         mathFriendlyEquals(1L, 1)
        !mathFriendlyEquals(null, 1)
        !mathFriendlyEquals(1L, null)
        !mathFriendlyEquals(12122333333333333L, 1)
    }

    def "should compare Strings using plain equals" () {
        expect:
         mathFriendlyEquals("s", "s")
        !mathFriendlyEquals("s", "nnn")
    }
}
