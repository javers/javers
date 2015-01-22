package org.javers.core.cases

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.MappingStyle
import org.javers.core.changelog.SimpleTextChangeLog
import org.javers.core.metamodel.object.InstanceIdDTO
import spock.lang.Ignore
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/76
 *
 * To resolve this issue we need to support types like List<List<String>>
 *
 * @author bartosz walacik
 */
class NestedListsTest extends Specification{
    @Ignore
    def "should support nested lists"() {
        given:
        def javers = JaversBuilder.javers().build()
        def cdo = new EntityWithNestedList(id:1,nestedList: [["A", "B", "C"], ["D", ".", "F"]])

        when:
        javers.commit("me@here.com", cdo)
        cdo.setNestedList([["A", "B", "C"], ["D", "E", "F"]])

        javers.commit("me@here.com", cdo);

        then:
        def changes = javers.getChangeHistory(InstanceIdDTO.instanceId(1, EntityWithNestedList.class), 5)
        def log = new SimpleTextChangeLog()
        javers.processChangeList(changes, log)
        println log.result()
    }
}
