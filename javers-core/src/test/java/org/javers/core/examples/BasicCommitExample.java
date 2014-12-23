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
    @Test
    public void shouldCommitToJaversRepository() {
        //given:

        // prepare JaVers instance. By default, JaVers uses InMemoryRepository,
        // it's useful for testing
        Javers javers = JaversBuilder.javers().build();

        // init your data
        Person robert = new Person("bob", "Robert Martin");
        // and persist initial commit
        javers.commit("user", robert);

        // do some changes
        robert.setName("Robert C.");
        // and persist another commit
        javers.commit("user", robert);

        // when:
        List<CdoSnapshot> snapshots =
            javers.getStateHistory(InstanceIdDTO.instanceId("bob", Person.class),10);

        // then:
        // there should be two Snapshots with Bob's state
        assertThat(snapshots).hasSize(2);
    }

    @Test
    public void shouldListChangeHistory() {
        // given:
        // commit some changes
        Javers javers = JaversBuilder.javers().build();
        Person robert = new Person("bob", "Robert Martin");
        javers.commit("user", robert);

        robert.setName("Robert C.");
        javers.commit("user", robert);

        // when:
        // list change history
        List<Change> changes =
            javers.getChangeHistory(InstanceIdDTO.instanceId("bob", Person.class), 5);

        // then:
        // there should be one ValueChange with Bob's firstName
        assertThat(changes).hasSize(1);
        ValueChange change = (ValueChange) changes.get(0);
        assertThat(change.getProperty().getName()).isEqualTo("name");
        assertThat(change.getLeft()).isEqualTo("Robert Martin");
        assertThat(change.getRight()).isEqualTo("Robert C.");
    }

    @Test
    public void shouldListStateHistory() {
        // given:
        // commit some changes
        Javers javers = JaversBuilder.javers().build();
        Person robert = new Person("bob", "Robert Martin");
        javers.commit("user", robert);

        robert.setName("Robert C.");
        javers.commit("user", robert);

        // when:
        // list state history - snapshots
        List<CdoSnapshot> snapshots =
            javers.getStateHistory(instanceId("bob", Person.class), 5);

        // then:
        // there should be two Snapshots with Bob's state
        assertThat(snapshots).hasSize(2);
        CdoSnapshot newState = snapshots.get(0);
        CdoSnapshot oldState = snapshots.get(1);
        assertThat(oldState.getPropertyValue("name")).isEqualTo("Robert Martin");
        assertThat(newState.getPropertyValue("name")).isEqualTo("Robert C.");
    }
}
