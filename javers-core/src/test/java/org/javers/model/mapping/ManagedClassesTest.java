package org.javers.model.mapping;

import org.javers.core.model.DummyNetworkAddress;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.fest.assertions.api.Assertions.assertThat;

public class ManagedClassesTest {

    private ManagedClasses managedClasses;
    private ManagedClass managedClass;

    @Before
    public void setUp() throws Throwable {
        managedClasses = new ManagedClasses();
        TypeMapper typeMapper = new TypeMapper();
        typeMapper.registerValueType(DummyNetworkAddress.class);

        managedClass = new ValueObject(DummyNetworkAddress.class, Collections.EMPTY_LIST);
    }

    @Test
    public void shouldAddManagedClass() throws Throwable {
        //when
        managedClasses.add(managedClass);

        //then
        assertThat(managedClasses.contains(managedClass)).isTrue();
    }

    @Test
    public void shouldContainManagedClassOfGivenSourceClass() throws Throwable {
        //given
        managedClasses.add(managedClass);

        //when
        boolean contain = managedClasses.containsManagedClassWithSourceClass(managedClass.getSourceClass());

        //then
        assertThat(contain).isTrue();
    }

    @Test
    public void shouldGetManagedClassBySourceClass() throws Throwable {
        //given
        managedClasses.add(managedClass);

        //when
        ManagedClass returnedManagedClass = managedClasses.getBySourceClass(managedClass.getSourceClass());

        //then
        assertThat(returnedManagedClass)
                .isNotNull()
                .isEqualTo(managedClass);
    }
}
