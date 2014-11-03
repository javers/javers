package org.javers.spring

import org.javers.core.Javers
import org.javers.spring.integration.AllMethodsAuditable
import org.javers.spring.integration.NonAuditableRepository
import spock.lang.Specification

import static org.fest.assertions.api.Assertions.assertThat

class JaversPostProcessorTest extends Specification {


    def "shouldn't proxy when bean don't have annotation"() {
        when:
        def bean = javersPostProcessor.postProcessBeforeInitialization(new NonAuditableRepository(), "name")

        then:
        bean.class == NonAuditableRepository
    }

    def "should proxy when class have annotation over method"() {
        when:
        def bean = javersPostProcessor.postProcessBeforeInitialization(new AllMethodsAuditable(), "name")

        then:
        assertThat(bean.class.toString()).contains("CGLIB")
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
        2 * javers.commit(_ as String, _ as Object)
    }
}
