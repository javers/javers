package org.javers.core.examples;

import org.fest.assertions.api.Assertions;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.examples.model.Employee;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author bartosz walacik
 */
public class EmployeeHierarchiesDiffExample {

    @Test
    public void shouldDetectSalaryChange(){
        //given:
        Javers javers = JaversBuilder.javers().build();

        Employee oldStructure = new Employee("Big Boss")
                .addSubordinates(new Employee("Noisy Manager"), new Employee("Great Developer", 10000));
        Employee newStructure = new Employee("Big Boss")
                .addSubordinates(new Employee("Noisy Manager"), new Employee("Great Developer", 20000));

        //when
        Diff diff = javers.compare(oldStructure, newStructure);

        //then
        //there should be one change of type {@link ValueChange}
        ValueChange change =  (ValueChange) diff.getChanges().get(0);

        assertThat(change.getAffectedCdoLocalId()).isEqualTo("Great Developer");
        assertThat(change.getProperty().getName()).isEqualTo("salary");
        assertThat(change.getLeft()).isEqualTo(10000);
        assertThat(change.getRight()).isEqualTo(20000);


        System.out.println("diff: " + javers.toJson(diff));
    }
}
