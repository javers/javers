package org.javers.core.prettyprint

import org.javers.core.JaversBuilder
import org.javers.core.model.DummyUserDetails
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class TypeMappingPrintDemo extends Specification {

    def "should pretty print JaVers types"() {

        when:
        def javers = JaversBuilder.javers().build()

        def t = javers.getTypeMapping(DummyUserDetails)

        println "toString: "+  t.toString()
        println "pretty: " + t.prettyPrint()

        then:
        true
    }
}
