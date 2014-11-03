package org.javers.spring

import org.javers.core.Javers
import org.javers.spring.integration.AllMethodsAuditable
import org.javers.spring.integration.SelectedMethodsAuditable
import spock.lang.Specification

class JaversPostProcessorTest extends Specification {

    def "should proxy methods with annotation"() {
        given:
        Javers javers = Mock()
        def ob1 = new Object()
        def ob2 = new Object()
        def javersPostProcessor = new JaversPostProcessor(javers)
        SelectedMethodsAuditable auditable = new SelectedMethodsAuditable()

        when:
        SelectedMethodsAuditable bean = javersPostProcessor.postProcessBeforeInitialization(auditable, "name")
        bean.auditableMethod(ob1)
        bean.nonAuditableMethod(ob2)

        then:
        1 * javers.commit(_ as String, ob1)
        0 * javers.commit(_ as String, ob2)
    }

    def "should proxy all methods when class have annotation over class"() {
        given:
        Javers javers = Mock()
        def ob1 = new Object()
        def ob2 = new Object()
        def javersPostProcessor = new JaversPostProcessor(javers)
        AllMethodsAuditable auditable = new AllMethodsAuditable()

        when:
        AllMethodsAuditable bean = javersPostProcessor.postProcessBeforeInitialization(auditable, "name")
        bean.auditableMethod(ob1)
        bean.auditableMethod2(ob2)

        then:
        1 * javers.commit(_ as String, ob1)
        1 * javers.commit(_ as String, ob2)
    }
}
