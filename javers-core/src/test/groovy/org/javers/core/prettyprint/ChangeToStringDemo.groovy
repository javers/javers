package org.javers.core.prettyprint

import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.container.ElementValueChange
import org.javers.core.diff.changetype.container.ValueAdded
import org.javers.core.diff.changetype.container.ValueRemoved
import org.javers.core.diff.changetype.map.EntryAdded
import org.javers.core.diff.changetype.map.EntryRemoved
import org.javers.core.diff.changetype.map.EntryValueChange
import org.javers.core.json.builder.ChangeTestBuilder
import org.javers.core.model.DummyUser
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author bartosz walacik
 */
class ChangeToStringDemo extends Specification {

    @Shared
    def cdo = new DummyUser(name:"1")

    @Unroll
    def "should pretty print #change.class.simpleName"() {
        given:

        when:
            def str = change.toString()
            println str

        then:
            str.length()>0 //it's a demo, not a real test

        where:
            change <<[
                ChangeTestBuilder.valueChange(cdo, "age", 10, 12),
                ChangeTestBuilder.referenceChanged(cdo, "supervisor", new DummyUser(name:2), new DummyUser(name:3)),
                ChangeTestBuilder.setChange(cdo,"stringSet",[new ValueAdded("addedStr"), new ValueRemoved("removedStr")]),
                ChangeTestBuilder.listChange(cdo,"integerList",[new ValueAdded(0,45), new ValueRemoved(1,45), new ElementValueChange(5, 44, 46)]),
                ChangeTestBuilder.mapChange(cdo,"valueMap",[new EntryAdded("key1","val1"),
                                                            new EntryRemoved("key2","val2"),
                                                            new EntryValueChange("key3","old1","new1")])
            ]
    }
}
