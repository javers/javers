package org.javers.spring

import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyNetworkAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import spock.lang.Specification

/**
 * @author Pawel Cierpiatka
 */
class JaversSpringFactoryTest extends Specification {

    def "should registered entity "() {
        given:
        JaversSpringFactory javersSpringFactory = new JaversSpringFactory()

        when:
        javersSpringFactory.setEntityClasses(Arrays.<Class>asList(DummyUser.class, DummyUserDetails.class))
        javersSpringFactory.setValueObject(Arrays.<Class>asList(DummyAddress.class, DummyNetworkAddress.class))

        then:
        javersSpringFactory.getObject().isManaged(DummyUser.class)
    }

    def "should registered described class with custom id"() {
        given:
        JaversSpringFactory javersSpringFactory = new JaversSpringFactory()
        Map<Class,String> describedEntityClasses = new HashMap<>()
        describedEntityClasses.put(DummyUser.class, "age")

        when:
        javersSpringFactory.setDescribedEntityClasses(describedEntityClasses)
        javersSpringFactory.setEntityClasses(Arrays.<Class>asList(DummyUserDetails.class))
        javersSpringFactory.setValueObject(Arrays.<Class>asList(DummyAddress.class, DummyNetworkAddress.class))

        then:
        javersSpringFactory.getObject().isManaged(DummyUser.class)
    }

}