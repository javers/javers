package org.javers.model.mapping;

import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.type.TypeMapper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.javers.test.assertion.Assertions.assertThat;


/**
 *
 * @author Adam Dubiel <adam.dubiel@gmail.com>
 */
public class EntityManagerBasicTest {

    private EntityManager entityManager;

    @BeforeMethod
    public void setUp() {
        entityManager = new EntityManager( new BeanBasedEntityFactory(new TypeMapper()));
    }

    @Test
    public void shouldThrowExceptionIfEntityIsNotManagedWhenTryingToGetIt() {

        //when
        catchException(entityManager).getByClass(NotManagedClass.class);

        // then
        assertThat((JaversException) caughtException()).hasCode(JaversExceptionCode.CLASS_NOT_MANAGED);
    }

    @Test
    public void shouldReturnEntityModelForManagedClassAfterMakingItManaged() {
        // given
        entityManager.manage(ManagedClass.class);

        // when
        Entity entity = entityManager.getByClass(ManagedClass.class);

        // then
        assertThat(entity).isNotNull();
    }

    @Test
    public void shouldReturnTrueForManagedClass() {
        // given
        entityManager.manage(ManagedClass.class);

        // when
        boolean isManaged = entityManager.isManaged(ManagedClass.class);

        // then
        assertThat(isManaged).isTrue();
    }

    private static class NotManagedClass { };

    private static class ManagedClass { };
}