package org.javers.model.mapping

import com.googlecode.catchexception.CatchException
import org.javers.core.exceptions.JaversException
import org.javers.core.exceptions.JaversExceptionCode
import org.javers.core.model.DummyManagedClass
import org.javers.core.model.DummyNetworkAddress
import org.javers.core.model.DummyNotManagedClass
import org.javers.model.mapping.type.TypeMapper
import org.javers.test.assertion.Assertions
import spock.lang.Specification

import static com.googlecode.catchexception.CatchException.catchException
import static com.googlecode.catchexception.CatchException.caughtException
import static org.fest.assertions.api.Assertions.assertThat
import static org.javers.test.assertion.Assertions.assertThat
import static org.mockito.Mockito.mock

class EntityManagerTest extends Specification{

    private EntityManager entityManager

    def setup() {
        TypeMapper mapper = new TypeMapper()
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner(mapper)
        EntityFactory entityFactory = new EntityFactory(scanner)
        ValueObjectFactory valueObjectFactory = new ValueObjectFactory()
        entityManager = new EntityManager(entityFactory, valueObjectFactory, mapper)
    }

    def "should throw exception if entity is not managed when trying to get it"() {

        when:
        catchException(entityManager).getByClass(DummyNotManagedClass)

        then:
        assertThat((JaversException) caughtException()).hasCode(JaversExceptionCode.CLASS_NOT_MANAGED)
    }

    def "should throw exception when class is registered but entity is not build"() {
        given:
        EntityDefinition entityDefinition = new EntityDefinition(DummyManagedClass)
        entityManager.registerEntity(entityDefinition)

        when:
        CatchException.catchException(entityManager).getByClass(DummyManagedClass)

        then:
        assertThat(caughtException()).overridingErrorMessage("No exception caught").isNotNull()
        assertThat((JaversException) caughtException()).hasCode(JaversExceptionCode.ENTITY_MANAGER_NOT_INITIALIZED)
    }

    def "should throw exception when class is mapped but is not instance of ManagedClass"() {
        when:
        CatchException.catchException(entityManager).getByClass(Integer)

        then:
        assertThat(caughtException()).overridingErrorMessage("No exception caught").isNotNull()
        assertThat((JaversException) caughtException()).hasCode(JaversExceptionCode.EXPECTED_ENTITY_OR_VALUE_OBJECT_SOURCE_CLASS)
    }

    def "should return entity model for managed class after Building it"() {
        given:
        EntityDefinition entityDefinition = new EntityDefinition(DummyManagedClass)
        entityManager.registerEntity(entityDefinition)
        entityManager.buildManagedClasses()

        when:
        ManagedClass entity = entityManager.getByClass(DummyManagedClass)

        then:
        assertThat(entity).isNotNull()
    }

    def "should return true for managed entity"() {
        given:
        EntityDefinition entityDefinition = new EntityDefinition(DummyManagedClass)
        entityManager.registerEntity(entityDefinition)
        entityManager.buildManagedClasses()

        when:
        boolean isManaged = entityManager.isManaged(DummyManagedClass)

        then:
        assertThat(isManaged).isTrue()
    }

    def "should return true for managed ValueObject"() {
        given:
        entityManager.registerValueObject(DummyNetworkAddress)
        entityManager.buildManagedClasses()

        when:
        boolean isManaged = entityManager.isManaged(DummyNetworkAddress)

        then:
        assertThat(isManaged).isTrue()
    }

    def "should not register entity in type mapper more than once"() {
        given:
        TypeMapper typeMapper = new TypeMapper()
        EntityDefinition managedClass = new EntityDefinition(DummyManagedClass)
        EntityManager entityManager = new EntityManager(mock(EntityFactory), mock(ValueObjectFactory), typeMapper)

        when:
        entityManager.registerEntity(managedClass)
        entityManager.registerEntity(managedClass)

        then:
        Assertions.assertThat(typeMapper.getMappedEntityReferenceTypes()).hasSize(1)
    }

    def "should not register value object more than once"() {
        given:
        Class alreadyMappedValueObject = DummyNetworkAddress
        TypeMapper typeMapper = new TypeMapper()
        EntityManager entityManager = new EntityManager(mock(EntityFactory), mock(ValueObjectFactory), typeMapper)

        when:
        entityManager.registerValueObject(alreadyMappedValueObject)
        entityManager.registerValueObject(alreadyMappedValueObject)

        then:
        Assertions.assertThat(typeMapper.getMappedValueObjectTypes()).hasSize(1)
    }
}
