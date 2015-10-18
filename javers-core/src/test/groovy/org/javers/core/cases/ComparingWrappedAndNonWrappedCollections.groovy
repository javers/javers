package org.javers.core.cases

import com.google.common.collect.Lists
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.Diff
import org.javers.core.model.DummyUser
import spock.lang.Specification

/**
 *
 * @see https://github.com/javers/javers/issues/176
 *
 * @author pawelszymczyk
 */
class ComparingWrappedAndNonWrappedCollections extends Specification {

    def "should return the same diff for wrapped and non wrapped collections"() {

        given:
        Javers javers = JaversBuilder.javers().build();

        when:
        Diff diffDirect = javers.compareCollections(oldVersion, currentVersion, DummyUser.class);
        Diff diffWithContainer = javers.compare(new ListContainer(oldVersion), new ListContainer(currentVersion));

        then:
        diffDirect.changes.size() == diffWithContainer.changes.size()

        where:
        oldVersion << [flatGraph(), flatGraph(), complexGraph(), complexGraph()]
        currentVersion << [flatGraphEntityRemoved(), flatGraphValueChanged(), complexGraphEntityRemoved(), complexGraphValueChanged()]
    }

    List<DummyUser> complexGraphValueChanged() {
        List<DummyUser> users = Lists.newArrayList(complexGraph())
        users.get(0).getEmployeesList().get(0).name == "new name"
        users.get(1).name == "new name"
        return users
    }

    List<DummyUser> complexGraphEntityRemoved() {
        List<DummyUser> users = Lists.newArrayList(complexGraph())
        users.get(0).getEmployeesList().remove(0)
        users.remove(1)
        return users
    }

    List<DummyUser> complexGraph() {
        def dummyUser1 = new DummyUser("ID1", "Value1")
        dummyUser1.addEmployee(new DummyUser("E1", "Employee1"))
        dummyUser1.addEmployee(new DummyUser("E2", "Employee2"))
        dummyUser1.addEmployee(new DummyUser("E3", "Employee3"))

        [dummyUser1, new DummyUser("ID2", "Value2"), new DummyUser("ID3", "Value3")]
    }

    List<DummyUser> flatGraphEntityRemoved() {
        List<DummyUser> users = Lists.newArrayList(flatGraph())
        users.remove(0)
        return users
    }

    List<DummyUser> flatGraphValueChanged() {
        List<DummyUser> users = Lists.newArrayList(flatGraph())
        users.get(0).name = "new name"
        return users
    }

    List<DummyUser> flatGraph() {
        [new DummyUser("ID1", "Value1"), new DummyUser("ID2", "Value2"), new DummyUser("ID3", "Value3")]
    }
}
