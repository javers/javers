package org.javers.spring.jpa

import groovy.sql.Sql
import org.javers.core.Javers
import org.javers.hibernate.entity.PersonCrudRepository
import org.javers.hibernate.entity.Person
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static org.javers.hibernate.integration.config.HibernateConfig.H2_URL

@ContextConfiguration(classes = MultipleTxManagersConfig)
class MultipleTxManagersTest extends Specification {
    @Autowired
    Javers javers

    @Autowired
    PersonCrudRepository repository

    def setup() {
        def sql = Sql.newInstance(H2_URL, "org.h2.Driver")
        sql.execute("DELETE jv_snapshot")
    }

    def "should not fail when there are more than one transaction manager in the application context"(){
        given:
        def person = new Person(id:"kaz")

        when:
        repository.save(person)

        then:
        javers.findSnapshots(QueryBuilder.byInstanceId("kaz", Person).build()).size() == 1
    }
}
