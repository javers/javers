package org.javers.core.snapshot

import org.javers.core.JaversTestBuilder
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class ObjectHasherTest extends Specification {
    @Shared ObjectHasher objectHasher

    def setupSpec(){
        def javers = JaversTestBuilder.javersTestAssembly()
        objectHasher = javers.objectHasher
    }

    def "should calculate hash of ValueObject "(){
      given:
      def address = new DummyAddress('Warsaw', 'Mokotowska')

      when:
      def hash = objectHasher.hash(address)

      then:
      hash == 'ba4a8532bc3fa2c16990e2a21e06cd1f'
    }

    def "should calculate hash of Entity "(){
        given:
        def snapshotEntity = new SnapshotEntity(id:1, intProperty:1, arrayOfEntities:[new SnapshotEntity(id:2)])

        when:
        def hash = objectHasher.hash(snapshotEntity)

        then:
        hash == 'ef202013ba2664f292cd28d525e34802'
    }
}
