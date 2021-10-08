package org.javers.core.metamodel.type

import org.javers.core.JaversTestBuilder
import org.javers.core.model.DummyAddress
import spock.lang.Specification

import static groovyx.gpars.GParsPool.withPool

/**
 * @author bartosz.walacik
 */
class TypeMapperConcurrentTest extends Specification {

    def "should not create more than one JaversType for given Java type, even if called from many threads "(){
      given:
      def javers = JaversTestBuilder.javersTestAssembly()
      def typeMapper = javers.typeMapper

      def hashes
      when:
      withPool 10, {
          hashes = (1..10).collectParallel {
                println "calling getJaversManagedType() in thread no. "+it
                def javersType = typeMapper.getJaversType(DummyAddress)
                System.identityHashCode(javersType)

          }
      }

      then:
      println hashes
      hashes.size() == 10
      new HashSet(hashes).size() == 1
    }
}