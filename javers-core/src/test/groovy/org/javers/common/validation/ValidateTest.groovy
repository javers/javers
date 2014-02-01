package org.javers.common.validation

import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class ValidateTest extends Specification {

    def "should check all varargs"() {
        when:
        Validate.argumentsAreNotNull(1, null)

        then:
        thrown IllegalArgumentException
    }
}
