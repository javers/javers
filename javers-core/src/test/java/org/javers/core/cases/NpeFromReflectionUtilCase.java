package org.javers.core.cases;

import org.fest.assertions.api.Assertions;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.annotation.Entity;
import org.javers.core.metamodel.annotation.Id;
import org.javers.repository.jql.ValueObjectIdDTO;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * https://github.com/javers/javers/issues/78
 *
 * test for NPE from {@link org.javers.common.reflection.ReflectionUtil#getAllFields(Class)}
 * when property type is an interface
 */
public class NpeFromReflectionUtilCase {

    @Test
    public void shouldSupportInterfaceProperty() {
        // given
        TestClassWithInterfaceProperty foo = new TestClassWithInterfaceProperty("1", new TestInterfaceImpl("Foo"));
        TestClassWithInterfaceProperty bar = new TestClassWithInterfaceProperty("1", new TestInterfaceImpl("Bar"));
        Javers javers = JaversBuilder.javers().build();

        // when
        Diff diff = javers.compare(foo, bar);

        System.out.println(diff);

        // then
        assertTrue(diff.getChanges().size() == 1);
        ValueChange change = diff.getChangesByType(ValueChange.class).get(0);
        ValueObjectIdDTO voId = ValueObjectIdDTO.valueObjectId("1", TestClassWithInterfaceProperty.class, "interfaceProperty");

        Assertions.assertThat(change.getAffectedGlobalId().value()).isEqualTo(voId.value());
        Assertions.assertThat(change.getPropertyName()).isEqualTo("value");
        Assertions.assertThat(change.getLeft()).isEqualTo("Foo");
        Assertions.assertThat(change.getRight()).isEqualTo("Bar");
    }

    @Entity
    class TestClassWithInterfaceProperty {
        @Id
        private String id;
        private TestInterface interfaceProperty;

        public TestClassWithInterfaceProperty(String id, TestInterfaceImpl interfaceProperty) {
            this.id = id;
            this.interfaceProperty = interfaceProperty;
        }
    }

    interface TestInterface {
        String getValue();
    }

    class TestInterfaceImpl implements TestInterface {

        private String value;

        public TestInterfaceImpl(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return "Foo";
        }
    }
}
