package org.javers.core.examples;

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
