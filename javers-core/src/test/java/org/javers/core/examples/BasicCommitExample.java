package org.javers.core.examples;

import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.examples.model.Employee;
import org.javers.core.examples.model.Person;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.junit.Test;
import java.util.List;
import static org.fest.assertions.api.Assertions.assertThat;

public class BasicCommitExample {
    @Test
    public void changesPrintingExampleWithGrouping() {
        // given
        Javers javers = JaversBuilder.javers().build();

        Employee sam = new Employee("Sam", 1_000);
        Employee frodo = new Employee("Frodo", 10_000);
        javers.commit("author", frodo);

        frodo.addSubordinate(sam);
        frodo.setSalary(11_000);
        sam.setSalary(2_000);
        javers.commit("author", frodo);

        // when
        Changes changes = javers.findChanges(QueryBuilder.byClass(Employee.class)
                .withNewObjectChanges().build());

        changes.groupByCommit().forEach(byCommit -> {
           System.out.println("commit " + byCommit.getCommit().getId());
           byCommit.groupByObject().forEach(byObject -> {
               System.out.println("  changes on " + byObject.getGlobalId().value() + " : ");
               byObject.get().forEach(change -> {
                   System.out.println("  - " + change);
               });
           });
        });

    }

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
        List<CdoSnapshot> snapshots = javers.findSnapshots(
            QueryBuilder.byInstanceId("bob", Person.class).build());

        // then:
        // there should be two Snapshots with Bob's state
        assertThat(snapshots).hasSize(2);
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
        // list state history - last 10 snapshots
        List<CdoSnapshot> snapshots = javers.findSnapshots(
            QueryBuilder.byInstanceId("bob", Person.class).limit(10).build());

        // then:
        // there should be two Snapshots with Bob's state
        assertThat(snapshots).hasSize(2);
        CdoSnapshot newState = snapshots.get(0);
        CdoSnapshot oldState = snapshots.get(1);
        assertThat(oldState.getPropertyValue("name")).isEqualTo("Robert Martin");
        assertThat(newState.getPropertyValue("name")).isEqualTo("Robert C.");
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
        List<Change> changes = javers.findChanges(
            QueryBuilder.byInstanceId("bob", Person.class).build());

        // then:
        // there should be one ValueChange with Bob's firstName
        assertThat(changes).hasSize(1);
        ValueChange change = (ValueChange) changes.get(0);
        assertThat(change.getPropertyName()).isEqualTo("name");
        assertThat(change.getLeft()).isEqualTo("Robert Martin");
        assertThat(change.getRight()).isEqualTo("Robert C.");
    }
}
