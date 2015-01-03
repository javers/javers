package org.javers.core.examples;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.examples.model.Person;
import org.junit.Test;
import static org.fest.assertions.api.Assertions.assertThat;

public class BasicEntityDiffExample {
  @Test
  public void shouldCompareTwoEntityObjects() {
    //given
    Javers javers = JaversBuilder.javers().build();

    Person tommyOld = new Person("tommy", "Tommy Smart");
    Person tommyNew = new Person("tommy", "Tommy C. Smart");

    //when
    Diff diff = javers.compare(tommyOld, tommyNew);

    //then
    //there should be one change of type {@link ValueChange}
    ValueChange change = diff.getChangesByType(ValueChange.class).get(0);

    assertThat(diff.getChanges()).hasSize(1);
    assertThat(change.getProperty().getName()).isEqualTo("name");
    assertThat(change.getAffectedGlobalId().value()).isEqualTo("org.javers.core.examples.model.Person/tommy");
    assertThat(change.getLeft()).isEqualTo("Tommy Smart");
    assertThat(change.getRight()).isEqualTo("Tommy C. Smart");

    System.out.println(diff);
  }
}
