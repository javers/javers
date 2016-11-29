package org.javers.spring.jpa

import groovy.sql.Sql
import org.javers.core.Javers
import org.javers.hibernate.integration.config.HibernateConfig
import org.javers.hibernate.integration.entity.Person
import org.javers.hibernate.integration.entity.PersonCrudRepository
import org.javers.repository.jql.QueryBuilder
import org.javers.repository.sql.ConnectionProvider
import org.javers.repository.sql.JaversSqlRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import spock.lang.Specification

import static org.javers.hibernate.integration.config.HibernateConfig.H2_URL

/**
 * @author bartosz.walacik
 */
@IntegrationTest
@SpringApplicationConfiguration(classes = CacheEvictSpringConfig)
class CacheEvictTest extends Specification{

    @Autowired
    Javers javers

    @Autowired
    JaversSqlRepository javersSqlRepository

    @Autowired
    PersonCrudRepository repository

    @Autowired
    ErrorThrowingService errorThrowingService

    def setup() {
        def sql = Sql.newInstance(H2_URL, "org.h2.Driver")
        sql.execute("DELETE jv_snapshot")
    }

    def "should evict GlobalId PK Cache after rollback"(){
      given:
      def person = new Person(id:"kaz")

      when:
      repository.save(person)
      person.name = "kaz"
      errorThrowingService.saveAndThrow(person)

      then:
      def ex = thrown(RuntimeException)
      ex.message == "rollback"
      javers.findSnapshots(QueryBuilder.anyDomainObject().build()).size() == 1
      javersSqlRepository.globalIdPkCacheSize == 0
    }
}
