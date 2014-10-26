package org.javers.core.metamodel.clazz

import org.javers.core.metamodel.property.BeanBasedPropertyScanner
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Pawel Cierpiatka
 */

class ManagedClassFactoryTest extends Specification {

    def setupSpec() {
        BeanBasedPropertyScanner scanner = new BeanBasedPropertyScanner();
        managedClassFactory = new ManagedClassFactory(scanner, new ClassAnnotationsScanner());
    }

    @Shared
    def ManagedClassFactory managedClassFactory

    def "should create Entity with properties, ID property and reference to client's class"() {
        when:
        def entity = managedClassFactory.createEntity(DummyUser);

        then:
        entity instanceof Entity
        entity.getClientsClass() == DummyUser
        entity.properties.size() > 2
        entity.idProperty.name == "name"
    }

    def "should create ValueObject with properties and reference to client's class"() {
        when:
        def vo = managedClassFactory.createValueObject(DummyAddress);

        then:
        vo instanceof ValueObject
        vo.getClientsClass() == DummyAddress
        vo.properties.size() > 2
    }

    def "should map as ValueObject by default"(){
        when:
        def vo = managedClassFactory.inferFromAnnotations(DummyAddress)

        then:
        vo instanceof ValueObject
    }

    def "should map as Entity if property level @Id annotation is present"() {
        when:
        def vo = managedClassFactory.inferFromAnnotations(DummyUser)

        then:
        vo instanceof Entity
    }
}