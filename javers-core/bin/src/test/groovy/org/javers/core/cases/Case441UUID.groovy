package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.object.InstanceId
import spock.lang.Specification

class Case441UUID extends Specification {

    class Entity {
        @Id
        UUID id
        String val
    }

    def "should use UUID.toString() in InstanceId"(){
        when:
        def javers = JaversBuilder.javers().build()

        UUID u = UUID.randomUUID()

        def diff = javers.compare(new Entity(id:u, val:"a"), new Entity(id:u, val:"b"))

        then:
        InstanceId id = diff.changes[0].affectedGlobalId
        id.value().endsWith("/" + u)
    }
}
