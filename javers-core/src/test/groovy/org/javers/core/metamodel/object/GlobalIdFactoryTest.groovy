package org.javers.core.metamodel.object

import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyNetworkAddress
import org.javers.core.model.SnapshotEntity
import org.javers.repository.jql.ValueObjectIdDTO
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.JaversTestBuilder.javersTestAssembly

/**
 * @author bartosz.walacik
 */
class GlobalIdFactoryTest extends Specification {

    @Shared
    GlobalIdFactory globalIdFactory = javersTestAssembly().globalIdFactory

    @Unroll
    def "should infer valueObjectType from path when path is #pathType"(){
      when:
      def id = globalIdFactory.createFromDto(ValueObjectIdDTO.valueObjectId(1, SnapshotEntity, path))

      then:
      id.typeName == DummyAddress.name

      where:
      path <<     ["valueObjectRef",  "mapPrimitiveToVO/HOME", "listOfValueObjects/0"]
      pathType << ["simple property", "map.property/map.key",  "list.property/list.index"]
    }

    @Unroll
    def "should parse valueObjectType from path for embedded ValueObject (#pathType)"() {
        when:
        def id = globalIdFactory.createFromDto(ValueObjectIdDTO.valueObjectId(1, SnapshotEntity, path))

        then:
        id.typeName == DummyNetworkAddress.name

        where:
        path <<     ["valueObjectRef/networkAddress",
                     "listOfValueObjects/0/networkAddress",
                     "mapPrimitiveToVO/HOME/networkAddress"]
        pathType << ["property/property", "list.property/list.index/property", "map.property/map.key/property"]
    }
}
