package org.javers.core.examples;

import org.javers.common.collections.Lists;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.examples.model.Person;
import org.junit.Test;
import java.util.List;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author bartosz.walacik
 */
public class ComparingTopLevelCollectionExample {

  @Test
  public void shouldDeeplyCompareTwoTopLevelCollections() {
    //given
    Javers javers = JaversBuilder.javers().build();

    List<Person> oldList = Lists.asList( new Person("tommy", "Tommy Smart") );
    List<Person> newList = Lists.asList( new Person("tommy", "Tommy C. Smart") );

    //when
    Diff diff = javers.compareCollections(oldList, newList, Person.class);

    //then
    //there should be one change of type {@link ValueChange}
    ValueChange change = diff.getChangesByType(ValueChange.class).get(0);

    assertThat(diff.getChanges()).hasSize(1);
    assertThat(change.getPropertyName()).isEqualTo("name");
    assertThat(change.getLeft()).isEqualTo("Tommy Smart");
    assertThat(change.getRight()).isEqualTo("Tommy C. Smart");

    System.out.println(diff);
  }
}
