package org.javers.core.cases
import com.google.common.collect.Lists
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.diff.Diff
import org.javers.core.model.DummyUser
import org.javers.core.model.ListContainer
import org.javers.core.model.SetContainer
import spock.lang.Specification

/**
 * @autho pawelszymczyk
 */
class ComparingWrappedAndNonWrappedCollections extends Specification {

    def "should return the same diff for wrapped and non wrapped collections"() {

        given:
        Javers javers = JaversBuilder.javers().build();

        when:
        Diff diffDirect = javers.compareCollections(oldVersion, currentVersion, DummyUser.class);
        Diff diffWithContainer = javers.compare(wrap(oldVersion), wrap(currentVersion));

        then:
        diffDirect.changes.size() == diffWithContainer.changes.size()

        where:
        oldVersion              || currentVersion
        flatList()              || flatListEntityRemoved()
        flatList()              || flatListValueChanged()
        complexList()           || complexListEntityRemoved()
        complexList()           || complexListValueChanged()

        flatList().toSet()      || flatListEntityRemoved().toSet()
        flatList().toSet()      || flatListValueChanged().toSet()
        complexList().toSet()   || complexListEntityRemoved().toSet()
        complexList().toSet()   || complexListValueChanged().toSet()
    }

    def wrap(def dummyUsers) {
        if (dummyUsers instanceof List) {
            return new ListContainer(dummyUsers)
        } else if (dummyUsers instanceof Set) {
            return new SetContainer(dummyUsers);
        }

        throw new RuntimeException("Expected Set or List, found: " + dummyUsers.class.simpleName)
    }

    List<DummyUser> complexListValueChanged() {
        List<DummyUser> users = Lists.newArrayList(complexList())
        users.get(0).getEmployeesList().get(0).name == "new name"
        users.get(1).name == "new name"
        return users
    }

    List<DummyUser> complexListEntityRemoved() {
        List<DummyUser> users = Lists.newArrayList(complexList())
        users.get(0).getEmployeesList().remove(0)
        users.remove(1)
        return users
    }

    List<DummyUser> complexList() {
        def dummyUser1 = new DummyUser("ID1", "Value1")
        dummyUser1.addEmployee(new DummyUser("E1", "Employee1"))
        dummyUser1.addEmployee(new DummyUser("E2", "Employee2"))
        dummyUser1.addEmployee(new DummyUser("E3", "Employee3"))

        [dummyUser1, new DummyUser("ID2", "Value2"), new DummyUser("ID3", "Value3")]
    }

    List<DummyUser> flatListEntityRemoved() {
        List<DummyUser> users = Lists.newArrayList(flatList())
        users.remove(0)
        return users
    }

    List<DummyUser> flatListValueChanged() {
        List<DummyUser> users = Lists.newArrayList(flatList())
        users.get(0).name = "new name"
        return users
    }


    List<DummyUser> flatList() {
        [new DummyUser("ID1", "Value1"), new DummyUser("ID2", "Value2"), new DummyUser("ID3", "Value3")]
    }
}
