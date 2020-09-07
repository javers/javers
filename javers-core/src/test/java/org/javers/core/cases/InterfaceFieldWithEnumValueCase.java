package org.javers.core.cases;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class InterfaceFieldWithEnumValueCase {

    @Test
    public void shouldCompareEnumFields() {
        TestClass foo = new TestClass(TestEnum.FOO);
        TestClass bar = new TestClass(TestEnum.BAR);

        Javers javers = JaversBuilder.javers().build();
        Diff diff = javers.compare(foo, bar);

        assertTrue(diff.getChanges().size() == 1);
    }


    interface TestInterface {
    }

    enum TestEnum implements TestInterface {
        FOO,
        BAR
    }

    class TestClass {
        private TestInterface enumVal;

        public TestClass(TestInterface enumVal) {
            this.enumVal = enumVal;
        }
    }
}
