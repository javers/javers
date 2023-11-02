package org.javers.core.cases;

import org.javers.core.Changes;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ElementValueChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * https://github.com/javers/javers/issues/1206
 *
 * @author Edgars Garsneks
 */
public class Case1206ArraysNpe {

    static class BeanWithArray {

        private final String[] items;

        public BeanWithArray(String... items) {
            this.items = items;
        }

    }

    @Test
    public void shouldNotThrowNPEWhenSamplingNullRight() {
        BeanWithArray a = new BeanWithArray("a");
        BeanWithArray b = new BeanWithArray(new String[]{null});

        Javers javers = JaversBuilder.javers().build();

        try {
            Diff diff = javers.compare(a, b);
            Changes changes = diff.getChanges();

            Assertions.assertEquals(1, changes.size());

            ArrayChange change = (ArrayChange) changes.get(0);
            ElementValueChange elementChange = (ElementValueChange) change.getChanges().get(0);

            Assertions.assertEquals(a.items[0], elementChange.getLeftValue());
            Assertions.assertEquals(b.items[0], elementChange.getRightValue());
        } catch (Exception e) {
            fail("Should not throw exception when sampling item.", e);
        }
    }

    @Test
    public void shouldNotThrowNPEWhenSamplingNullLeft() {
        BeanWithArray a = new BeanWithArray(new String[]{null});
        BeanWithArray b = new BeanWithArray("a");

        Javers javers = JaversBuilder.javers().build();

        try {
            Diff diff = javers.compare(a, b);
            Changes changes = diff.getChanges();

            Assertions.assertEquals(1, changes.size());

            ArrayChange change = (ArrayChange) changes.get(0);
            ElementValueChange elementChange = (ElementValueChange) change.getChanges().get(0);

            Assertions.assertEquals(a.items[0], elementChange.getLeftValue());
            Assertions.assertEquals(b.items[0], elementChange.getRightValue());
        } catch (Exception e) {
            fail("Should not throw exception when sampling item.", e);
        }
    }


}
