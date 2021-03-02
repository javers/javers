package org.javers.core.cases

import org.javers.common.exception.JaversException
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import java.time.LocalDateTime
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.common.exception.JaversExceptionCode.COMMITTING_TOP_LEVEL_VALUES_NOT_SUPPORTED
import static org.javers.common.exception.JaversExceptionCode.COMPARING_TOP_LEVEL_VALUES_NOT_SUPPORTED

/**
 * @author bartosz.walacik
 */
public class Case250BetterExceptionMessage extends Specification {
    @Unroll
    def "should throw nice error message when committing top-level value"() {
        given:
        Javers javers = JaversBuilder.javers().build();

        when:
        javers.commit("z",val)

        then:
        JaversException e = thrown()
        e.code == COMMITTING_TOP_LEVEL_VALUES_NOT_SUPPORTED
        println e.getMessage()

        where:
        val << ["String", 1, LocalDateTime.now()]
    }

    @Unroll
    def "should throw nice error message when comparing top-level values"() {
        given:
        Javers javers = JaversBuilder.javers().build();

        when:
        javers.compare(val, val);

        then:
        JaversException e = thrown()
        e.code == COMPARING_TOP_LEVEL_VALUES_NOT_SUPPORTED
        println e.getMessage()

        where:
        val << ["String", 1, LocalDateTime.now()]
    }
}
