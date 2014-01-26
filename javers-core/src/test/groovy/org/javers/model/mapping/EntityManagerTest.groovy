package org.javers.model.mapping

import org.javers.core.exceptions.JaversException
import org.javers.core.exceptions.JaversExceptionCode
import org.javers.core.metamodel.property.BeanBasedPropertyScanner
import org.javers.core.model.DummyManagedClass
import org.javers.core.model.DummyNetworkAddress
import org.javers.core.model.DummyNotManagedClass
import org.javers.model.mapping.type.EntityReferenceType
import org.javers.model.mapping.type.TypeMapper
import org.javers.model.mapping.type.ValueObjectType
import spock.lang.Specification

class EntityManagerTest extends Specification{

    private EntityManager entityManager

    def setup() {
        TypeMapper mapper = new TypeMapper()
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner()
        ManagedClassFactory entityFactory = new ManagedClassFactory(scanner, mapper)
        entityManager = new EntityManager(entityFactory,mapper)
    }

    def "should throw exception if entity is not managed when trying to get it"() {

        when:
        entityManager.getByClass(DummyNotManagedClass)

        then:
        JaversException ex = thrown()
        ex.code == JaversExceptionCode.CLASS_NOT_MANAGED
    }

    def "should return entity model for managed class after building it"() {
        given:
        entityManager.registerEntity(DummyManagedClass)
        entityManager.buildManagedClasses()

        when:
        ManagedClass entity = entityManager.getByClass(DummyManagedClass)

        then:
        entity != null
    }

    def "should return true for managed entity"() {
        given:
        entityManager.registerEntity(DummyManagedClass)
        entityManager.buildManagedClasses()

        when:
        boolean isManaged = entityManager.isManaged(DummyManagedClass)

        then:
        isManaged == true
    }

    def "should return true for managed ValueObject"() {
        given:
        entityManager.registerValueObject(DummyNetworkAddress)
        entityManager.buildManagedClasses()

        when:
        boolean isManaged = entityManager.isManaged(DummyNetworkAddress)

        then:
        isManaged == true
    }

    def "should not register entity in typeMapper more than once"() {
        given:
        TypeMapper typeMapper = new TypeMapper()
        EntityManager entityManager = new EntityManager(Mock(ManagedClassFactory), typeMapper)

        when:
        entityManager.registerEntity(DummyManagedClass)
        entityManager.registerEntity(DummyManagedClass)

        then:
        typeMapper.getMappedTypes(EntityReferenceType).size() == 1
    }

    def "should not register valueObject in typeMapper more than once"() {
        given:
        TypeMapper typeMapper = new TypeMapper()
        EntityManager entityManager = new EntityManager(Mock(ManagedClassFactory), typeMapper)

        when:
        def mapped = typeMapper.getMappedTypes(ValueObjectType).size()
        entityManager.registerValueObject(DummyNetworkAddress)
        entityManager.registerValueObject(DummyNetworkAddress)

        then:
        typeMapper.getMappedTypes(ValueObjectType).size() == mapped+1
    }
}
