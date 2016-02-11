package org.javers.core.cases
import com.google.common.collect.Lists
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.MappingStyle
import org.javers.core.diff.Diff
import org.javers.core.model.DummyUser
import spock.lang.Specification
import spock.lang.Unroll

class SetContainer {
    Set<DummyUser> dummyUsers

    SetContainer(Set<DummyUser> dummyUsers) {
        this.dummyUsers = dummyUsers
    }
}

class ListContainer {
    List<DummyUser> dummyUsers

    ListContainer(List<DummyUser> dummyUsers) {
        this.dummyUsers = dummyUsers
    }
}

/**
 * @author pawelszymczyk
 */
class BeanScanning extends ComparingWrappedAndNonWrappedCollectionsTest {

    @Override
    Javers javers() {
        JaversBuilder.javers().withMappingStyle(MappingStyle.BEAN).build()
    }
}


class FieldScanning extends ComparingWrappedAndNonWrappedCollectionsTest {

    @Override
    Javers javers() {
        JaversBuilder.javers().withMappingStyle(MappingStyle.FIELD).build()
    }
}

abstract class ComparingWrappedAndNonWrappedCollectionsTest extends Specification {

    abstract Javers javers()

    @Unroll
    def "should return the same diff for wrapped and non wrapped collections"() {

        given:
        Javers javers = javers()

        when:
        Diff diffDirect = javers.compareCollections(oldVersion, currentVersion, DummyUser)
        Diff diffWithContainer = javers.compare(wrap(oldVersion), wrap(currentVersion))

        then:
        diffDirect.changes.size() == diffWithContainer.changes.size()

        where:
        oldVersion            | currentVersion
        flatList()            | flatListEntityRemoved()
        flatList()            | flatListValueChanged()
        complexList()         | complexListEntityRemoved()
        complexList()         | complexListValueChanged()

        flatList().toSet()    | flatListEntityRemoved().toSet()
        flatList().toSet()    | flatListValueChanged().toSet()
        complexList().toSet() | complexListEntityRemoved().toSet()
        complexList().toSet() | complexListValueChanged().toSet()
    }

    def wrap(def dummyUsers) {
        if (dummyUsers instanceof List) {
            return new ListContainer(dummyUsers)
        } else if (dummyUsers instanceof Set) {
            return new SetContainer(dummyUsers)
        }

        throw new RuntimeException("Expected Set or List, found: " + dummyUsers.class.simpleName)
    }

    List<DummyUser> complexListValueChanged() {
        List<DummyUser> users = complexList().collect()
        users.get(0).getEmployeesList().get(0).name == "new name"
        users.get(1).name == "new name"
        users
    }

    List<DummyUser> complexListEntityRemoved() {
        List<DummyUser> users = Lists.newArrayList(complexList())
        users.get(0).getEmployeesList().remove(0)
        users.remove(1)
        users
    }

    List<DummyUser> complexList() {
        def dummyUser1 = new DummyUser("ID1", "Value1")
        dummyUser1.addEmployee(new DummyUser("E1", "Employee1"))
        dummyUser1.addEmployee(new DummyUser("E2", "Employee2"))
        dummyUser1.addEmployee(new DummyUser("E3", "Employee3"))

        [dummyUser1, new DummyUser("ID2", "Value2"), new DummyUser("ID3", "Value3")]
    }

    List<DummyUser> flatListEntityRemoved() {
        List<DummyUser> users = flatList().collect()
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
