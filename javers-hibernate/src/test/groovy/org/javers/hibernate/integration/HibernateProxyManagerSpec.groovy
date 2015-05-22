package org.javers.hibernate.integration

import org.hibernate.Hibernate
import org.hibernate.proxy.HibernateProxy
import org.javers.core.Javers
import org.javers.hibernate.integration.config.JaversProxyManagerApplicationConfig
import org.javers.hibernate.integration.entity.Person
import org.javers.hibernate.integration.entity.PersonCrudRepository
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification

class HibernateProxyManagerSpec extends Specification {

    @Shared
    AnnotationConfigApplicationContext context

    @Shared
    Javers javers

    @Shared
    PersonCrudRepository repository

    def setupSpec() {
        context = new AnnotationConfigApplicationContext(JaversProxyManagerApplicationConfig)
        javers = context.getBean(Javers)
        repository = context.getBean(PersonCrudRepository)
    }

    def "should unproxy hibernate entity and save it to Javers repository"() {
        def person1 = new Person("1", "kaz")
        def person2 = new Person("2", "pawel")
        person1.boss = person2
        given:
        repository.save(person2)
        repository.save(person1)

        when:
        def person = repository.findOne("1")
        person.name = "bartosz"

        repository.save(person)

        then:
        repository.findOne("1").name == "bartosz"
    }

}