package org.javers.spring

import spock.lang.Specification

import static java.lang.reflect.Proxy.isProxyClass


class JaversPostProcessorTest extends Specification {

    def javersPostProcessor = new JaversPostProcessor(Stub(AspectFactory))

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
        isProxyClass(bean.getClass())
    }

    class NonAuditableRepository {

    }

    @JaversAudit
    class AuditableRepository {

    }
}
