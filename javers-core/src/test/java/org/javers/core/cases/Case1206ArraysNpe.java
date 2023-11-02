package org.javers.core.cases;

import org.javers.core.JaversBuilder;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ElementValueChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * see https://github.com/javers/javers/issues/1206
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
        var a = new BeanWithArray("a");
        var b = new BeanWithArray(new String[]{null});

        var javers = JaversBuilder.javers().build();

        try {
            var diff = javers.compare(a, b);
            var changes = diff.getChanges();

            Assertions.assertEquals(1, changes.size());

            var change = (ArrayChange) changes.get(0);
            var elementChange = (ElementValueChange) change.getChanges().get(0);

            Assertions.assertEquals(a.items[0], elementChange.getLeftValue());
            Assertions.assertEquals(b.items[0], elementChange.getRightValue());
        } catch (Exception e) {
            fail("Should not throw exception when sampling item.", e);
        }
    }

    @Test
    public void shouldNotThrowNPEWhenSamplingNullLeft() {
        var a = new BeanWithArray(new String[]{null});
        var b = new BeanWithArray("a");

        var javers = JaversBuilder.javers().build();

        try {
            var diff = javers.compare(a, b);
            var changes = diff.getChanges();

            Assertions.assertEquals(1, changes.size());

            var change = (ArrayChange) changes.get(0);
            var elementChange = (ElementValueChange) change.getChanges().get(0);

            Assertions.assertEquals(a.items[0], elementChange.getLeftValue());
            Assertions.assertEquals(b.items[0], elementChange.getRightValue());
        } catch (Exception e) {
            fail("Should not throw exception when sampling item.", e);
        }
    }


}
