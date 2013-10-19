package org.javers.common.reflection.org.javers.model.mapping;

import com.googlecode.catchexception.CatchException;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.model.mapping.BeanBasedEntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.ManagedClass;
import org.javers.model.mapping.type.TypeMapper;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Id;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.javers.test.assertion.Assertions.assertThat;


/**
 *
 * @author Adam Dubiel <adam.dubiel@gmail.com>
 */
public class EntityManagerBasicTest {

    private EntityManager entityManager;

    @Before
    public void setUp() {
        TypeMapper mapper = new TypeMapper();
        entityManager = new EntityManager(new BeanBasedEntityFactory(mapper));
    }

    @Test
    public void shouldThrowExceptionIfEntityIsNotManagedWhenTryingToGetIt() {

        //when
        catchException(entityManager).getByClass(NotManagedClass.class);

        // then
        assertThat((JaversException) caughtException()).hasCode(JaversExceptionCode.CLASS_NOT_MANAGED);
    }

    @Test
    public void shouldThrowExceptionWhenClassIsRegisteredButEntityIsNotBuild() {
        // given
        entityManager.registerEntity(DummyManagedClass.class);

        // when
        CatchException.catchException(entityManager).getByClass(DummyManagedClass.class);

        //then
        assertThat(caughtException()).overridingErrorMessage("No exception caught").isNotNull();
        assertThat((JaversException) caughtException()).hasCode(JaversExceptionCode.ENTITY_MANAGER_NOT_INITIALIZED);
    }

    @Test
    public void shouldReturnEntityModelForManagedClassAfterBuildingIt() {
        // given
        entityManager.registerEntity(DummyManagedClass.class);
        entityManager.buildManagedClasses();

        // when
        ManagedClass entity = entityManager.getByClass(DummyManagedClass.class);

        // then
        assertThat(entity).isNotNull();
    }

    @Test
    public void shouldReturnTrueForManagedClass() {
        // given
        entityManager.registerEntity(DummyManagedClass.class);
        entityManager.buildManagedClasses();
        // when
        boolean isManaged = entityManager.isManaged(DummyManagedClass.class);

        // then
        assertThat(isManaged).isTrue();
    }

    private static class NotManagedClass { };

    private static class DummyManagedClass {
        @Id
        private int getId() {
            return 0;
        }
    };
}