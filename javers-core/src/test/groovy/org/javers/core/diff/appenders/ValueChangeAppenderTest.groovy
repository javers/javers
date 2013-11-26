package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.NodePair
import org.javers.core.model.DummyUser
import org.javers.model.domain.changeType.ValueChange
import org.javers.model.mapping.Entity
import org.javers.model.mapping.Property
import org.javers.model.object.graph.ObjectNode

import static org.javers.core.model.DummyUser.Sex.FEMALE
import static org.javers.core.model.DummyUser.Sex.OCCASIONALLY

/**
 * @author bartosz walacik
 */
class ValueChangeAppenderTest extends AbstractDiffTest {

    def "should not append valueChange when values are equal" () {
        given:
        ObjectNode left =  buildDummyUserNode("1", FEMALE)
        ObjectNode right = buildDummyUserNode("1", FEMALE)
        Property sex = getEntity(DummyUser).getProperty("sex")

        when:
        Collection<ValueChange> changes =
            new ValueChangeAppender().calculateChanges(new NodePair(left,right),sex);

        then:
        changes.size() == 0
    }

    def "should append Enum valueChange" () {
        given:
        ObjectNode left =  buildDummyUserNode("1", FEMALE)
        ObjectNode right = buildDummyUserNode("1", OCCASIONALLY)
        Property sex = getEntity(DummyUser).getProperty("sex")

        when:
        Collection<ValueChange> changes =
            new ValueChangeAppender().calculateChanges(new NodePair(left,right),sex);

        then:
        //TODO clever custom assertion
        changes.size() == 1
        ValueChange change = changes.iterator().next()
        change.property == sex
        change.globalCdoId.localCdoId == "1"
        change.leftValue.value == FEMALE
        change.rightValue.value == OCCASIONALLY
    }

    //todo add test for other simple types

    //todo add test for ValueObject

}
