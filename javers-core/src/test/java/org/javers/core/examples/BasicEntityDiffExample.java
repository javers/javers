package org.javers.core.examples;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.examples.model.Address;
import org.javers.core.examples.model.Employee;
import org.javers.core.examples.model.EmployeeBuilder;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.javers.core.diff.ListCompareAlgorithm.LEVENSHTEIN_DISTANCE;

public class BasicEntityDiffExample {

  @Test
  public void shouldCompareTwoEntities() {
    //given
    Javers javers = JaversBuilder.javers()
            .withListCompareAlgorithm(LEVENSHTEIN_DISTANCE)
            .build();

    Employee tommyOld = EmployeeBuilder.Employee("Frodo")
            .withAge(40)
            .withPosition("Townsman")
            .withSalary(10_000)
            .withPrimaryAddress(new Address("Shire"))
            .withSkills("management")
            .withSubordinates(new Employee("Sam"))
            .build();

    Employee tommyNew = EmployeeBuilder.Employee("Frodo")
            .withAge(41)
            .withPosition("Hero")
            .withBoss(new Employee("Gandalf"))
            .withPrimaryAddress(new Address("Mordor"))
            .withSalary(12_000)
            .withSkills("management", "agile coaching")
            .withSubordinates(new Employee("Sméagol"), new Employee("Sam"))
            .build();

    //when
    Diff diff = javers.compare(tommyOld, tommyNew);

    //then
    assertThat(diff.getChanges()).hasSize(9);

    // diff pretty print
    System.out.println(diff);

    // diff as JSON
    System.out.println(javers.getJsonConverter().toJson(diff));
  }
}
