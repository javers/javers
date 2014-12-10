package org.javers.core.examples;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.examples.model.Person;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.InstanceIdDTO;
import org.junit.Test;
import java.util.List;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId;

public class BasicCommitExample {
    /**
     * JaVers by default uses InMemoryRepository, it's useful for testing
     */
    @Test
    public void shouldListChangeHistory() {

        //given:
        //0. prepare JaVers instance
        Javers javers = JaversBuilder.javers().build();

        //1. init your data
        Person robert = new Person("bob", "Robert Martin");
        //2. persist initial commit
        javers.commit("user", robert);

        //3. do some changes
        robert.setName("Robert C.");
        //4. and commit
        javers.commit("user", robert);

        //when:
        //5. list change history - diffs
        List<Change> changes = javers.getChangeHistory(InstanceIdDTO.instanceId("bob", Person.class), 5);

        //then:
        //6. there should be one ValueChange on Bob's firstName, stored in JaversRepository
        assertThat(changes).hasSize(1);
        assertThat(changes.get(0)).isInstanceOf(ValueChange.class);
        ValueChange change = (ValueChange)changes.get(0);
        assertThat(change.getProperty().getName()).isEqualTo("name");
        assertThat(change.getLeft() ).isEqualTo("Robert Martin");
        assertThat(change.getRight()).isEqualTo("Robert C.");

        //when:
        //7. list state history - snapshots
        List<CdoSnapshot> snapshots = javers.getStateHistory(instanceId("bob", Person.class), 5);

        //then:
        //8. there should be two Snapshots of Bob state, stored in JaversRepository
        assertThat(snapshots).hasSize(2);
        CdoSnapshot newState = snapshots.get(0);
        CdoSnapshot oldState = snapshots.get(1);
        assertThat(oldState.getPropertyValue("name")).isEqualTo("Robert Martin");
        assertThat(newState.getPropertyValue("name")).isEqualTo("Robert C.");
        assertThat(oldState.getPropertyValue("login")).isEqualTo("bob");
        assertThat(newState.getPropertyValue("login")).isEqualTo("bob");
    }
}
