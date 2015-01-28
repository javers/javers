package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.changelog.SimpleTextChangeLog
import org.javers.core.diff.changetype.ValueChange
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

    def "should support collections with generic item type"() {
        given:
        def javers = JaversBuilder.javers().build()
        def cdo = new EntityWithNestedList(id:1,listWithGenericItem: [threadLocal("a")])

        when:
        javers.commit("me@here.com", cdo)
        cdo.setListWithGenericItem([threadLocal("b")])

        javers.commit("me@here.com", cdo)

        then:
        def changes = javers.getChangeHistory(InstanceIdDTO.instanceId(1, EntityWithNestedList.class), 5)
        changes[0] instanceof ValueChange
        changes[0].left  == threadLocal("a")
        changes[0].right == threadLocal("b")
    }

    @Ignore
    def "should support nested lists"() {
        given:
        def javers = JaversBuilder.javers().build()
        def cdo = new EntityWithNestedList(id:1,nestedList: [["A", "B", "C"], ["D", ".", "F"]])

        when:
        javers.commit("me@here.com", cdo)
        cdo.setNestedList([["A", "B", "C"], ["D", "E", "F"]])

        javers.commit("me@here.com", cdo)

        then:
        def changes = javers.getChangeHistory(InstanceIdDTO.instanceId(1, EntityWithNestedList.class), 5)
        def log = new SimpleTextChangeLog()
        javers.processChangeList(changes, log)
        println log.result()
    }

    def threadLocal(def what) {
        def t = new ThreadLocal()
        t.set(what)
        t
    }
}
