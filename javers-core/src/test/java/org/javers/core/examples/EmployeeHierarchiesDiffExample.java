package org.javers.core.examples;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.*;
import org.javers.core.examples.model.Employee;
import org.junit.Test;
import static org.fest.assertions.api.Assertions.assertThat;

public class EmployeeHierarchiesDiffExample {

  /** {@link NewObject} example */
  @Test
  public void shouldDetectHired() {
    //given
    Javers javers = JaversBuilder.javers().build();

    Employee oldBoss = new Employee("Big Boss")
        .addSubordinates(
            new Employee("Great Developer"));

    Employee newBoss = new Employee("Big Boss")
        .addSubordinates(
            new Employee("Great Developer"),
            new Employee("Hired One"),
            new Employee("Hired Second"));

    //when
    Diff diff = javers.compare(oldBoss, newBoss);

    //then
    assertThat(diff.getObjectsByChangeType(NewObject.class))
        .hasSize(2)
        .containsOnly(new Employee("Hired One"),
                      new Employee("Hired Second"));

    System.out.println(diff);
  }

  /** {@link ObjectRemoved} example */
  @Test
  public void shouldDetectFired() {
    //given
    Javers javers = JaversBuilder.javers().build();

    Employee oldBoss = new Employee("Big Boss")
            .addSubordinates(
                    new Employee("Great Developer"),
                    new Employee("Team Lead").addSubordinates(
                            new Employee("Another Dev"),
                            new Employee("To Be Fired")
                    ));

    Employee newBoss = new Employee("Big Boss")
            .addSubordinates(
                    new Employee("Great Developer"),
                    new Employee("Team Lead").addSubordinates(
                            new Employee("Another Dev")
                    ));

    //when
    Diff diff = javers.compare(oldBoss, newBoss);

    //then
    assertThat(diff.getChangesByType(ObjectRemoved.class)).hasSize(1);

    System.out.println(diff);
  }

  /** {@link ValueChange} example */
  @Test
  public void shouldDetectSalaryChange(){
    //given
    Javers javers = JaversBuilder.javers().build();

    Employee oldBoss = new Employee("Big Boss")
            .addSubordinates(
                    new Employee("Noisy Manager"),
                    new Employee("Great Developer", 10000));

    Employee newBoss = new Employee("Big Boss")
            .addSubordinates(
                    new Employee("Noisy Manager"),
                    new Employee("Great Developer", 20000));

    //when
    Diff diff = javers.compare(oldBoss, newBoss);

    //then
    ValueChange change =  diff.getChangesByType(ValueChange.class).get(0);

    assertThat(change.getAffectedLocalId()).isEqualTo("Great Developer");
    assertThat(change.getPropertyName()).isEqualTo("salary");
    assertThat(change.getLeft()).isEqualTo(10000);
    assertThat(change.getRight()).isEqualTo(20000);

    System.out.println(diff);
  }

  /** {@link ReferenceChange} example */
  @Test
  public void shouldDetectBossChange() {
    //given
    Javers javers = JaversBuilder.javers().build();

    Employee oldBoss = new Employee("Big Boss")
        .addSubordinates(
             new Employee("Manager One")
                 .addSubordinate(new Employee("Great Developer")),
             new Employee("Manager Second"));

    Employee newBoss = new Employee("Big Boss")
        .addSubordinates(
             new Employee("Manager One"),
             new Employee("Manager Second")
                 .addSubordinate(new Employee("Great Developer")));

    //when
    Diff diff = javers.compare(oldBoss, newBoss);

    //then
    ReferenceChange change = diff.getChangesByType(ReferenceChange.class).get(0);

    assertThat(change.getAffectedLocalId()).isEqualTo("Great Developer");
    assertThat(change.getLeft().value()).endsWith("Manager One");
    assertThat(change.getRight().value()).endsWith("Manager Second");

    System.out.println(diff);
  }
}
