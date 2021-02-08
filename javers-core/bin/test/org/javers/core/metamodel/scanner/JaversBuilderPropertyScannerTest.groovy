package org.javers.core.metamodel.scanner

import org.javers.core.JaversBuilder
import org.javers.core.MappingStyle
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers

/**
 * @author bartosz.walacik
 */
class JaversBuilderPropertyScannerTest extends Specification{
    def "should load default properties file"() {
        given:
        JaversBuilder javersBuilder = javers()

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(PropertyScanner) instanceof FieldBasedPropertyScanner
    }

    def "should contain FieldBasedPropertyScanner when Field style"() {
        given:
        JaversBuilder javersBuilder = javers().withMappingStyle(MappingStyle.FIELD)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(PropertyScanner) instanceof FieldBasedPropertyScanner
    }

    def "should contain BeanBasedPropertyScanner when Bean style"() {
        given:
        JaversBuilder javersBuilder = javers().withMappingStyle(MappingStyle.BEAN)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(PropertyScanner) instanceof BeanBasedPropertyScanner
    }
}
