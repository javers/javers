package org.javers.spring.sql

import org.hibernate.Hibernate
import org.javers.core.Javers
import org.javers.spring.boot.ShallowEntity
import org.javers.spring.boot.ShallowEntityRepository
import org.javers.spring.boot.TestApplication
import org.javers.spring.boot.DummyEntity
import org.javers.spring.boot.DummyEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class HibernateSmartUnproxyTest extends Specification{
    @Autowired
    Javers javers

    @Autowired
    DummyEntityRepository dummyEntityRepository

    @Autowired
    ShallowEntityRepository shallowEntityRepository

    def "should not initialize proxy of Shallow reference"(){
      given:
      def shallowEntity = ShallowEntity.random()
      shallowEntityRepository.save(shallowEntity)
      def proxy = shallowEntityRepository.getOne(shallowEntity.id)

      println "proxy.isInitialized: " + Hibernate.isInitialized(proxy)
      println "proxy.class: " + proxy.class
      println "proxy.id: " + proxy.id
      println "proxy.persistenClass: "+ proxy.getHibernateLazyInitializer().getPersistentClass()
      println "proxy.isInitialized: " + Hibernate.isInitialized(proxy)
      println 'I am happy :)'

      assert !Hibernate.isInitialized(proxy)

      when:
      def entity = DummyEntity.random()
      entity.setShallowEntity(proxy)
      dummyEntityRepository.save(entity)

      then:
      assert !Hibernate.isInitialized(proxy)
    }
}
