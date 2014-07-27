package org.javers.core.examples;

import org.fest.assertions.api.Assertions;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder ;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.Property;
import org.junit.Test;
import spock.lang.Specification;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId;

/**
 * @author bartosz walacik
 */
public class BasicDiffExample {

    @Test
    public void shouldCompareTwoObjects() {

        //given:
        Javers javers = JaversBuilder.javers().build();

        Address a1 = new Address("New York","5th Avenue");
        Address a2 = new Address("New York","6th Avenue");

        //when:
        Diff diff = javers.compare(a1, a2);

        //then:
        println("diff: "+ javers.toJson(diff));

        /** There should be one change of type {@link ValueChange} */
        assertThat(diff.getChanges()).hasSize(1);
        assertThat(diff.getChanges().get(0)).isInstanceOf(ValueChange.class);

        /** and it should record change on 'street' {@link Property} */
        ValueChange change =  (ValueChange)diff.getChanges().get(0);
        println ("propertyName: "+change.getProperty().getName());
        println ("old value:    "+change.getLeft());
        println ("new value:    "+change.getRight());
        assertThat(change.getProperty().getName()).isEqualTo("street");
        assertThat(change.getLeft() ).isEqualTo("5th Avenue");
        assertThat(change.getRight()).isEqualTo("6th Avenue");
    }

    private void println(String text){
        System.out.println(text);
    }

}
