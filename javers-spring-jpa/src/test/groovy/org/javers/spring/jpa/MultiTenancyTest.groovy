package org.javers.spring.jpa

import groovy.sql.Sql
import org.javers.core.Javers
import org.javers.hibernate.entity.Person
import org.javers.hibernate.entity.PersonCrudRepository
import org.javers.hibernate.integration.config.TenantContext
import org.javers.repository.jql.QueryBuilder
import org.javers.repository.sql.JaversSqlRepository
import org.javers.repository.sql.session.KeyGenerator
import org.javers.repository.sql.session.Sequence
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import javax.sql.DataSource

import static org.javers.hibernate.integration.config.HibernateConfig.*

@ContextConfiguration(classes = MultiTenancyConfig)
class MultiTenancyTest extends Specification {

    private static final long JV_COMMIT_PK_SEQ_PRIMARY = 1000
    private static final long JV_COMMIT_PK_SEQ_SECONDARY = 10000

    @Autowired
    Javers javers

    @Autowired
    PersonCrudRepository repository

    @Autowired
    JaversSqlRepository sqlRepository

    @Autowired
    @Qualifier("dataSource")
    DataSource dataSource

    @Autowired
    @Qualifier("secondaryDataSource")
    DataSource secondaryDataSource


    def "should use one sequence map per database"() {
        given:
        def sequenceAllocation = ((KeyGenerator.SequenceAllocation) sqlRepository.getSessionFactory().getKeyGenerator())
        sequenceAllocation.reset()
        def sequences = sequenceAllocation.getSequences()
        def sql = Sql.newInstance(H2_URL, "org.h2.Driver")
        sql.execute(String.format("DELETE jv_snapshot;alter sequence jv_commit_pk_seq restart with %s;", JV_COMMIT_PK_SEQ_PRIMARY))
        sql = Sql.newInstance(H2_SECONDARY_URL, "org.h2.Driver")
        sql.execute(String.format("DELETE jv_snapshot;alter sequence jv_commit_pk_seq restart with %s;", JV_COMMIT_PK_SEQ_SECONDARY))

        when:
        TenantContext.setTenant(TENANT1)
        repository.save(new Person(id: "Peter Dinklage"))
        TenantContext.setTenant(TENANT2)
        repository.save(new Person(id: "Sophie Turner"))
        TenantContext.setTenant(TENANT1)
        repository.save(new Person(id: "Rory McCann"))

        then:
        sequences.containsKey(H2_URL)
        sequences.containsKey(H2_SECONDARY_URL)
        TenantContext.setTenant(TENANT1)
        javers.findSnapshots(QueryBuilder.byClass(Person.class).build()).size() == 2
        TenantContext.setTenant(TENANT2)
        javers.findSnapshots(QueryBuilder.byClass(Person.class).build()).size() == 1

        ((Map<String, Sequence>) sequences.get(H2_URL))
                .get("jv_commit_pk_seq").nextLocalValue() == Long.sum(JV_COMMIT_PK_SEQ_PRIMARY * 100, 2)

        ((Map<String, Sequence>) sequences.get(H2_SECONDARY_URL))
                .get("jv_commit_pk_seq").nextLocalValue() == Long.sum(JV_COMMIT_PK_SEQ_SECONDARY * 100, 1)
    }
}
