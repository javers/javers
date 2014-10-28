package org.javers.spring

import spock.lang.Specification

import static java.lang.reflect.Proxy.isProxyClass
import static org.fest.assertions.api.Assertions.assertThat


class JaversPostProcessorTest extends Specification {

    def javersPostProcessor = new JaversPostProcessor()

    def "should do nothing when bean dont have annotation"() {
        when:
        def bean = javersPostProcessor.postProcessBeforeInitialization(new NonAuditableRepository(), "name")

        then:
        bean.class == NonAuditableRepository
    }

    def "should proxy when have annotation"() {
        when:
        def bean = javersPostProcessor.postProcessBeforeInitialization(new AuditableRepository(), "name")

        then:
        assertThat(bean.class.toString()).contains("EnhancerBySpringCGLIB")
    }

    class NonAuditableRepository {

    }


    class AuditableRepository {

        @JaversAudit
        void save() {

        }
    }
}
