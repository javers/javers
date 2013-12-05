package org.javers.model.mapping;

import com.googlecode.catchexception.CatchException;
import org.javers.core.exceptions.JaversException;
import org.javers.core.exceptions.JaversExceptionCode;
import org.javers.core.model.DummyNetworkAddress;
import org.javers.core.model.DummyUser;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.test.assertion.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.verification.VerificationMode;

import javax.persistence.Id;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.fest.assertions.api.Assertions.fail;
import static org.javers.test.assertion.Assertions.assertThat;
import static org.mockito.Mockito.*;


/**
 *
 * @author Adam Dubiel <adam.dubiel@gmail.com>
 */
public class EntityManagerBasicTest1 {

    private EntityManager entityManager;

    @Before
    public void setUp() {
        TypeMapper mapper = new TypeMapper();
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(mapper);
        EntityFactory entityFactory = new EntityFactory(scanner);
        ValueObjectFactory valueObjectFactory = new ValueObjectFactory();
        entityManager = new EntityManager(entityFactory, valueObjectFactory, mapper);
    }


    @Test
    public void shouldThrowExceptionIfEntityIsNotManagedWhenTryingToGetIt() {

        //when
        catchException(entityManager).getByClass(NotManagedClass.class);

        // then
        assertThat((JaversException) caughtException()).hasCode(JaversExceptionCode.CLASS_NOT_MAPPED);
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
    public void shouldReturnTrueForManagedEntity() {
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

    @Test
    public void shouldReturnTrueForManagedValueObject() {
        // given
        entityManager.registerValueObject(DummyNetworkAddress.class);
        entityManager.buildManagedClasses();

        // when
        boolean isManaged = entityManager.isManaged(DummyNetworkAddress.class);

        // then
        assertThat(isManaged).isTrue();
    }

    @Test
    public void shouldNotRegisterEntityInTypeMapperMoreThanOnce() throws Throwable {
        //given
        TypeMapper typeMapper = new TypeMapper();
        EntityManager entityManager = new EntityManager(mock(EntityFactory.class), mock(ValueObjectFactory.class), typeMapper);

        //when
        entityManager.registerEntity(DummyUser.class);
        entityManager.registerEntity(DummyUser.class);

        //then
        Assertions.assertThat(typeMapper.getMappedEntityReferenceTypes()).hasSize(1);
    }

    @Test
    public void shouldNotRegisterValueObjectMoreThanOnce() throws Throwable {
        //given
        Class alreadyMappedValueObject = DummyNetworkAddress.class;
        TypeMapper typeMapper = mock(TypeMapper.class);
        when(typeMapper.isMapped(alreadyMappedValueObject)).thenReturn(true);
        EntityManager entityManager = new EntityManager(mock(EntityFactory.class), mock(ValueObjectFactory.class), typeMapper);

        //when
        entityManager.registerValueObject(alreadyMappedValueObject);

        //then
        verify(typeMapper, hadNoInteractionWith()).registerValueObjectType(alreadyMappedValueObject);
    }

    private VerificationMode hadNoInteractionWith() {
        return times(0);
    }
}