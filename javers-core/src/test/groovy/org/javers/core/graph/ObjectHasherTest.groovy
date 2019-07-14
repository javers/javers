package org.javers.core.graph

import org.javers.core.JaversTestBuilder
import org.javers.core.graph.ObjectHasher
import org.javers.core.model.DummyAddress
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class ObjectHasherTest extends Specification {
    @Shared ObjectHasher objectHasher
    @Shared JaversTestBuilder javers

    def setupSpec(){
        javers = JaversTestBuilder.javersTestAssembly()
        objectHasher = new ObjectHasher(javers.snapshotFactory, javers.jsonConverter)
    }

    def "should calculate hash of ValueObject "(){
        given:
        def address = new DummyAddress('Warsaw', 'Mokotowska')

        when:
        def node = javers.createLiveNode(address)
        def hash = objectHasher.hash([node.cdo])

        then:
        hash == 'ba4a8532bc3fa2c16990e2a21e06cd1f'
        hash == javers.hash(address)
    }
}
