package org.javers.core.examples;

import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.changelog.SimpleTextChangeLog;
import org.javers.core.diff.Change;
import org.javers.core.examples.model.Employee;
import org.javers.repository.jql.QueryBuilder;
import org.junit.Test;
import java.util.List;

public class ChangeLogExample {

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

        //then
        System.out.println("Printing the flat list of Changes :");
        changes.forEach(change -> System.out.println("- " + change));

        //then
        System.out.println("Changes prettyPrint :");
        System.out.println(changes.prettyPrint());

        System.out.println("Printing Changes with grouping by commits and by objects :");
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
    public void shouldPrintTextChangeLog() {
        // given:
        Javers javers = JaversBuilder.javers().build();
        Employee bob = new Employee("Bob", 9_000, "ScrumMaster");
        javers.commit("hr.manager", bob);

        // do some changes and commit
        bob.setPosition("Developer");
        bob.setSalary(11_000);
        javers.commit("hr.director", bob);

        bob.addSubordinates(new Employee("Trainee One"), new Employee("Trainee Two"));
        javers.commit("hr.manager", bob);

        // when:
        List<Change> changes = javers.findChanges(
            QueryBuilder.byInstanceId("Bob", Employee.class).build());
        String changeLog = javers.processChangeList(changes, new SimpleTextChangeLog());

        // then:
        System.out.println(changeLog);
    }
}
