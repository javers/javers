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
}
