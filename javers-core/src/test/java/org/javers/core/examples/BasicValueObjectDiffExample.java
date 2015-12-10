package org.javers.core.examples;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.examples.model.Address;
import org.junit.Test;
import static org.fest.assertions.api.Assertions.assertThat;

public class BasicValueObjectDiffExample {

  @Test
  public void shouldCompareTwoObjects() {

    //given
    Javers javers = JaversBuilder.javers().build();

    Address address1 = new Address("New York","5th Avenue");
    Address address2 = new Address("New York","6th Avenue");

    //when
    Diff diff = javers.compare(address1, address2);

    //then
    //there should be one change of type {@link ValueChange}
    ValueChange change = diff.getChangesByType(ValueChange.class).get(0);

    assertThat(diff.getChanges()).hasSize(1);
    assertThat(change.getAffectedGlobalId().value())
              .isEqualTo("org.javers.core.examples.model.Address/");
    assertThat(change.getPropertyName()).isEqualTo("street");
    assertThat(change.getLeft()).isEqualTo("5th Avenue");
    assertThat(change.getRight()).isEqualTo("6th Avenue");

    System.out.println(diff);
  }
}
